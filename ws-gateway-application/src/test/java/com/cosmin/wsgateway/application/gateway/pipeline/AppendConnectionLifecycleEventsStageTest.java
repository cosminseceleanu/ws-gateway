package com.cosmin.wsgateway.application.gateway.pipeline;

import com.cosmin.wsgateway.domain.events.Connected;
import com.cosmin.wsgateway.domain.events.Disconnected;
import com.cosmin.wsgateway.domain.events.InboundEvent;
import com.cosmin.wsgateway.domain.events.UserMessage;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class AppendConnectionLifecycleEventsStageTest {
    private static final String CONNECTION_ID = "connectionId";

    private final AppendConnectionLifecycleEventsStage subject = AppendConnectionLifecycleEventsStage.newInstance(CONNECTION_ID);

    @Test
    public void testApply_inboundFlux_shouldStartWithConnectEvent() {
        UserMessage userMessage = new UserMessage(CONNECTION_ID, "", "");
        Flux<InboundEvent> initial = Flux.just(userMessage);

        var result = subject.apply(initial);

        StepVerifier.create(result)
                .expectNext(new Connected(CONNECTION_ID))
                .expectNext(userMessage)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void testApply_inboundFlux_shouldEndWithDisconnectEvent() {
        UserMessage userMessage = new UserMessage(CONNECTION_ID, "", "");
        Flux<InboundEvent> initial = Flux.just(userMessage);

        var result = subject.apply(initial);

        StepVerifier.create(result)
                .expectNextCount(2)
                .expectNext(new Disconnected(CONNECTION_ID))
                .verifyComplete();
    }
}