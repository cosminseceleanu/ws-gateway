package com.cosmin.wsgateway.application.gateway.pipeline;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.events.TopicMessage;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class TransformReceivedOutboundEventsStageTest {
    @Mock
    private PayloadTransformer transformer;

    @Mock
    private GatewayMetrics gatewayMetrics;

    @Mock
    private Connection.Context context;

    private TransformReceivedOutboundEventsStage subject;

    @BeforeEach
    void setUp() {
        subject = TransformReceivedOutboundEventsStage.newInstance(transformer, gatewayMetrics, context);
    }

    private static Stream<Arguments> provideTopicMessages() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("hello", "hello"),
                Arguments.of("\"foo\":\"bar\"}", Map.of("foo", "bar")),
                Arguments.of("1", 1),
                Arguments.of("[1, 2, 3]", List.of(1, 2, 3))
        );
    }

    @ParameterizedTest
    @MethodSource("provideTopicMessages")
    public void testApply_rawMessageFlux_shouldBeTransformedToTopicMessage(String json, Object expectedPayload) {
        when(transformer.toPayload(eq(json), any())).thenReturn(expectedPayload);
        when(context.getEndpoint()).thenReturn(Endpoint.builder().build());
        when(context.getId()).thenReturn("id");

        var initial = Flux.just(json);
        var result = subject.apply(initial);

        StepVerifier.create(result)
                .expectNext(new TopicMessage("id", expectedPayload))
                .verifyComplete();

        verify(gatewayMetrics, times(1)).recordOutboundEventReceived(any(), anyString());
    }
}