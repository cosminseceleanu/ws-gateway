package com.cosmin.wsgateway.application.gateway.pipeline.operators;

import static org.mockito.Mockito.when;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import com.cosmin.wsgateway.application.gateway.connection.Message;
import com.cosmin.wsgateway.domain.Event;
import com.cosmin.wsgateway.domain.events.TopicMessage;
import com.cosmin.wsgateway.domain.events.UserMessage;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SendToUserOperatorTest {
    @Mock
    private PayloadTransformer transformer;

    @Mock
    private GatewayMetrics gatewayMetrics;

    @Mock
    private Connection.Context context;

    private SendToUserOperator subject;

    @BeforeEach
    void setUp() {
        subject = new SendToUserOperator(transformer, gatewayMetrics, context);
    }

    @Test
    public void testApply_outboundEventFlux_shouldBeTransformedToRawMessage() {
        var e1 = new TopicMessage("id", List.of(1, 2));
        var e2 = new TopicMessage("id","hello");
        when(transformer.fromPayload(List.of(1, 2))).thenReturn("[1, 2]");
        when(transformer.fromPayload("hello")).thenReturn("\"hello\"");

        Flux<Event> initial = Flux.just(e1, e2);
        var result = subject.apply(initial);

        StepVerifier.create(result)
                .expectNext(Message.text("[1, 2]"))
                .expectNext(Message.text("\"hello\""))
                .verifyComplete();
    }

    @Test
    public void testApply_shouldDiscardNonOutboundEvents() {
        var e1 = new TopicMessage("id", List.of(1, 2));
        var e2 = new UserMessage("1", "a", "a");
        when(transformer.fromPayload(List.of(1, 2))).thenReturn("[1, 2]");

        Flux<Event> initial = Flux.just(e1, e2);
        var result = subject.apply(initial);

        StepVerifier.create(result)
                .expectNext(Message.text("[1, 2]"))
                .verifyComplete();
    }
}