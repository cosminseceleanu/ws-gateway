package com.cosmin.wsgateway.application.gateway.pipeline.operators;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cosmin.wsgateway.application.Fixtures;
import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import com.cosmin.wsgateway.application.gateway.connector.BackendConnector;
import com.cosmin.wsgateway.application.gateway.connector.ConnectorResolver;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.EndpointConfiguration;
import com.cosmin.wsgateway.domain.Route;
import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.backends.HttpSettings;
import com.cosmin.wsgateway.domain.backends.KafkaBackend;
import com.cosmin.wsgateway.domain.backends.KafkaSettings;
import com.cosmin.wsgateway.domain.events.UserMessage;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SendToBackendsOperatorTest {
    @Mock
    private ConnectorResolver connectorResolver;

    @Mock
    private GatewayMetrics gatewayMetrics;

    @Mock
    private Connection.Context context;

    @Mock
    private BackendConnector httpConnector;

    @Mock
    private BackendConnector kafkaConnector;

    @Mock
    private GatewayMetrics.Stopwatch stopwatch;

    private SendToBackendsOperator subject;


    @BeforeEach
    void setUp() {
        when(gatewayMetrics.measureMono(any(), any(), any(), anyMap())).thenAnswer(a -> a.getArgument(1));
        when(gatewayMetrics.startTimer(any(), any())).thenReturn(stopwatch);

        subject = new SendToBackendsOperator(connectorResolver, context, gatewayMetrics);
    }

    @Test
    public void testApply_endpointHasMultipleBackends_shouldSendEventToEachBackend() {
        Backend<KafkaSettings> kafkaBackend = KafkaBackend.ofDefaults("topic1", "localhost");
        Backend<HttpSettings> httpBackend = HttpBackend.ofDefaults("localhost1");

        var endpoint = Fixtures.defaultEndpoint()
                .withConfiguration(EndpointConfiguration.ofRoutes(Set.of(
                        Route.defaultRoute(Set.of(httpBackend, kafkaBackend))
                )));
        when(context.getEndpoint()).thenReturn(endpoint);
        when(kafkaConnector.sendEvent(any(), any())).thenAnswer(a -> Mono.just(a.getArgument(0)));
        when(httpConnector.sendEvent(any(), any())).thenAnswer(a -> Mono.just(a.getArgument(0)));

        when(connectorResolver.getConnector(kafkaBackend)).thenReturn(kafkaConnector);
        when(connectorResolver.getConnector(httpBackend)).thenReturn(httpConnector);

        var event = new UserMessage("id", List.of(1, 2), "[1, 2]");
        var result = subject.apply(Flux.fromIterable(List.of(event)));

        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();

        verify(httpConnector, times(1)).sendEvent(event, httpBackend);
        verify(kafkaConnector, times(1)).sendEvent(event, kafkaBackend);
    }

    @Test
    public void testApply_givenAFluxWithMultipleEvents_shouldMeasureTimeForEachEvent() {
        var endpoint = Fixtures.defaultEndpoint();
        when(context.getEndpoint()).thenReturn(endpoint);
        when(httpConnector.sendEvent(any(), any())).thenAnswer(a -> Mono.just(a.getArgument(0)));
        when(connectorResolver.getConnector(any())).thenReturn(httpConnector);

        var result = subject.apply(Flux.fromIterable(List.of(
                new UserMessage("id", List.of(1, 2), "[1, 2]"),
                new UserMessage("id2", List.of(1, 2), "[1, 2]")
        )));

        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();

        verify(stopwatch, times(2)).stop();
    }

    @Test
    public void testApply_whenBackendThrowsError_shouldResumeWithBackendError() {
        var endpoint = Fixtures.defaultEndpoint();
        when(context.getEndpoint()).thenReturn(endpoint);
        when(connectorResolver.getConnector(any())).thenReturn(httpConnector);
        when(gatewayMetrics.measureMono(any(), any(), any(), anyMap())).thenReturn(Mono.error(new RuntimeException("Test")));

        var result = subject.apply(Flux.fromIterable(List.of(
                new UserMessage("id", List.of(1, 2), "[1, 2]")
        )));

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
        verify(gatewayMetrics, times(1)).recordBackendError(any(), any(), any());
    }
}