package com.cosmin.wsgateway.application.gateway.connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cosmin.wsgateway.application.Fixtures;
import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PubSub;
import com.cosmin.wsgateway.application.gateway.pipeline.StagesProvider;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.GeneralSettings;
import com.cosmin.wsgateway.domain.events.InboundEvent;
import com.cosmin.wsgateway.domain.events.OutboundEvent;
import com.cosmin.wsgateway.domain.events.TopicMessage;
import com.cosmin.wsgateway.domain.events.UserMessage;
import java.time.Duration;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

@ExtendWith(MockitoExtension.class)
class ConnectionTest {

    public static final String CONNECTION_ID = "id";
    @Mock
    private PubSub pubSub;

    @Mock
    private GatewayMetrics gatewayMetrics;

    @Mock
    private StagesProvider stagesProvider;

    @Mock
    private PubSub.Subscription subscription;

    private Endpoint defaultEndpoint = Fixtures.defaultEndpoint();

    private Connection subject;

    @BeforeEach
    void setUp() {
        when(stagesProvider.appendConnectionLifecyleEventsStage(anyString())).thenReturn(Function.identity());
        when(stagesProvider.transformMessageStage(anyString()))
                .thenReturn(f -> f.map(msg -> new UserMessage(CONNECTION_ID, msg.getPayload(), msg.getPayload())));

        when(stagesProvider.sendEventToBackendsStage(any())).thenReturn(f -> f.map(MockOutboundEvent::of));
        when(stagesProvider.transformReceivedOutboundEventsStage(any()))
                .thenReturn(f -> f.map(msg -> new TopicMessage(CONNECTION_ID, msg)));
        when(stagesProvider.sendEventToUserStage(any())).thenReturn(f -> f.map(o -> Message.text(o.payload().toString())));

        when(subscription.getEvents()).thenReturn(Flux.empty());
        when(pubSub.subscribe(anyString())).thenReturn(subscription);

        Connection.Context context = Connection.Context.newInstance(CONNECTION_ID, getEndpoint());
        subject = new Connection(pubSub, context, gatewayMetrics, stagesProvider);
    }

    @Test
    public void testHandle_whenConnectionReceiveUserMessages_shouldForwardEventToBackends() {
        var testPublisher = TestPublisher.<Message>create();
        var result = subject.handle(testPublisher.flux());

        StepVerifier.create(result)
                .then(() -> testPublisher.next(Message.text("m1"), Message.text("m2")))
                .expectNext(Message.text("m1"))
                .expectNext(Message.text("m2"))
                .then(testPublisher::complete)
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
    public void testHandle_whenOutboundMessageFailsToBeProcessed_shouldContinueTheProcessing() {
        var inboundPublisher = TestPublisher.<Message>create();
        var outboundPublisher = TestPublisher.<String>create();

        when(stagesProvider.transformReceivedOutboundEventsStage(any()))
                .thenReturn(f -> f.map(msg -> new TopicMessage(CONNECTION_ID, msg)))
                .thenThrow(new RuntimeException("Test"));

        when(subscription.getEvents()).thenReturn(outboundPublisher.flux());

        var result = subject.handle(inboundPublisher.flux());

        StepVerifier.create(result)
                .then(() -> outboundPublisher.next("o1"))
                .expectNext(Message.text("o1"))
                .then(() -> outboundPublisher.error(new RuntimeException("aaaa")))
//                .then(() -> outboundPublisher.next("o2")) fix this
//                .expectNext(Message.text("o2"))
                .then(inboundPublisher::complete)
                .expectComplete()
                .verify();
    }

    @Test
    public void testHandle_outboundMessages_shouldBeSentToUser() {
        when(stagesProvider.sendEventToBackendsStage(any())).thenReturn(f -> f.map(MockOutboundEvent::of));

        var result = subject.handle(Flux.just(Message.text("i1")));

        StepVerifier.create(result)
                .expectNext(Message.text("i1"))
                .expectComplete()
                .verify();
    }

    @Test
    public void testHandle_whenNoMessageIsReceived_shouldCloseConnection() {
        Endpoint endpoint = getEndpoint();
        Connection.Context context = Connection.Context.newInstance(CONNECTION_ID, endpoint);
        subject = new Connection(pubSub, context, gatewayMetrics, stagesProvider);
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
    public void testHandle_whenMessagesAreReceived_shouldKeepAliveConnection() {
        Endpoint endpoint = getEndpoint();
        Connection.Context context = Connection.Context.newInstance(CONNECTION_ID, endpoint);
        subject = new Connection(pubSub, context, gatewayMetrics, stagesProvider);
        var testPublisher = TestPublisher.<Message>create();

        var result = subject.handle(testPublisher.flux());

        StepVerifier.create(result)
                .thenAwait(Duration.ofSeconds(1))
                .expectNext(Message.ping())
                .then(() -> testPublisher.next(Message.text("text")))
                .expectNext(Message.text("text"))
                .thenAwait(Duration.ofSeconds(1))
                .expectNext(Message.ping())
                .then(() -> testPublisher.next(Message.pong()))
                .expectNext(Message.ping())
                .thenAwait(Duration.ofSeconds(1))
                .expectNext(Message.ping())
                .thenCancel()
                .verify();
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

        private final InboundEvent event;

        @Override
        public String connectionId() {
            return event.connectionId();
        }

        @Override
        public Object payload() {
            return event.payload();
        }
    }
}