package com.cosmin.wsgateway.application.gateway.connection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cosmin.wsgateway.application.Fixtures;
import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PubSub;
import com.cosmin.wsgateway.application.gateway.pipeline.operators.Operators;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.Event;
import com.cosmin.wsgateway.domain.GeneralSettings;
import com.cosmin.wsgateway.domain.events.OutboundEvent;
import com.cosmin.wsgateway.domain.events.UserMessage;
import java.time.Duration;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConnectionTest {

    private static final String CONNECTION_ID = "id";

    @Mock
    private PubSub pubSub;

    @Mock
    private GatewayMetrics gatewayMetrics;

    @Mock
    private Operators operators;

    @Mock
    private PubSub.Subscription subscription;

    private Endpoint defaultEndpoint = Fixtures.defaultEndpoint();

    private Connection subject;

    private final Function<Flux<Message>, Flux<Event>> inboundFlowOperator = f -> f.map(m -> new UserMessage(CONNECTION_ID, m.getPayload(), m.getPayload()));

    private final Function<Flux<String>, Flux<Event>> outboundFlowOperator = f -> f.map(MockOutboundEvent::of);

    private final Function<Flux<Event>, Flux<Message>> sendToUserOperator = f -> f.map(e -> Message.text(e.payload().toString()));

    @BeforeEach
    void setUp() {
        when(operators.inboundFlow(any())).thenReturn(inboundFlowOperator);
        when(operators.outboundFlow(any())).thenReturn(outboundFlowOperator);
        when(operators.sendToUser(any())).thenReturn(sendToUserOperator);

        when(subscription.getEvents()).thenReturn(Flux.empty());
        when(pubSub.subscribe(anyString())).thenReturn(subscription);

        Connection.Context context = Connection.Context.newInstance(CONNECTION_ID, getEndpoint());
        subject = new Connection(pubSub, context, operators, gatewayMetrics);
    }

    @Test
    public void testHandle_whenConnectionReceiveUserMessages_shouldForwardEventToBackends() {
        var result = subject.handle(Flux.just(Message.text("m1"), Message.text("m2")));

        StepVerifier.create(result)
                .expectNext(Message.text("m1"))
                .expectNext(Message.text("m2"))
                .expectComplete()
                .verify();
    }

    @Test
    public void testHandle_whenFluxIsStarted_shouldSubscribeToTopic() {
        subject.handle(Flux.empty());

        verify(pubSub, times(1)).subscribe(Connection.getOutboundTopic(CONNECTION_ID));
    }

    @Test
    public void testHandle_whenFluxIsCompleted_shouldUnsubscribeFromTopic() {
        Flux<String> fluxTopic = Flux.empty();
        when(subscription.getEvents()).thenReturn(fluxTopic);
        var result = subject.handle(Flux.just(Message.text("m1")));

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(pubSub, times(1)).unsubscribe(subscription);
    }

    @Test
    public void testHandle_whenFluxIsCompleted_shouldMarkConnectionAsClosed() {
        var result = subject.handle(Flux.empty());

        StepVerifier.create(result).verifyComplete();

        Assertions.assertTrue(subject.isClosed());
    }

    @Test
    public void testHandle_onError_shouldCompleteSuccessful() {
        var testPublisher = TestPublisher.<Message>create();

        var result = subject.handle(testPublisher.flux());

        StepVerifier.create(result)
                .then(() -> testPublisher.error(new RuntimeException("test")))
                .expectComplete()
                .verify();

        verify(gatewayMetrics, times(1)).recordError(any(), any());
    }

    @Test
    public void testHandle_whenConnectionReceiveOutboundMessages_shouldBeSentToUser() {
        var testPublisher = TestPublisher.<Message>create();

        when(subscription.getEvents()).thenReturn(Flux.just("o1"));

        var result = subject.handle(testPublisher.flux());

        StepVerifier.create(result)
                .expectNext(Message.text("o1"))
                .then(testPublisher::complete)
                .expectComplete()
                .verify();
    }

    @Test
    public void testHandle_whenNoMessageIsReceived_shouldCloseConnection() {
        Endpoint endpoint = getEndpoint();
        Connection.Context context = Connection.Context.newInstance(CONNECTION_ID, endpoint);
        subject = new Connection(pubSub, context, operators, gatewayMetrics);
        var testPublisher = TestPublisher.<Message>create();

        var result = subject.handle(testPublisher.flux());

        StepVerifier.create(result)
                .thenAwait(Duration.ofSeconds(1))
                .expectNext(Message.ping())
                .thenAwait(Duration.ofSeconds(1))
                .expectNext(Message.ping())
                .expectNext(Message.poisonPill("Missed heartbeats"))
                .then(testPublisher::complete)
                .expectComplete()
                .verify();
    }

    @Test
    public void testHandle_whenMessagesAreReceived_shouldKeepConnectionAlive() {
        Endpoint endpoint = getEndpoint();
        Connection.Context context = Connection.Context.newInstance(CONNECTION_ID, endpoint);
        subject = new Connection(pubSub, context, operators, gatewayMetrics);
        var testPublisher = TestPublisher.<Message>create();

        var result = subject.handle(testPublisher.flux());

        StepVerifier.create(result)
                .thenAwait(Duration.ofSeconds(1))
                .expectNext(Message.ping())
                .then(() -> testPublisher.next(Message.text("text1"), Message.text("text2")))
                .expectNext(Message.text("text1"))
                .expectNext(Message.text("text2"))
                .thenAwait(Duration.ofSeconds(1))
                .expectNext(Message.ping())
                .then(() -> testPublisher.next(Message.pong()))
                .then(testPublisher::complete)
                .verifyComplete();

        Assertions.assertEquals(0, subject.getMissedPings());
    }

    private Endpoint getEndpoint() {
        return Fixtures.defaultEndpoint()
                .withConfiguration(defaultEndpoint.getConfiguration()
                        .withGeneralSettings(GeneralSettings.ofDefaults()
                                .toBuilder()
                                .heartbeatIntervalInSeconds(1)
                                .heartbeatMaxMissingPingFrames(2)
                                .build()));
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    @ToString
    private static class MockOutboundEvent implements OutboundEvent {

        private final String rawMessage;

        @Override
        public String connectionId() {
            return CONNECTION_ID;
        }

        @Override
        public Object payload() {
            return rawMessage;
        }
    }
}