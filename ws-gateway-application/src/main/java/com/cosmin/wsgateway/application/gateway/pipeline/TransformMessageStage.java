package com.cosmin.wsgateway.application.gateway.pipeline;

import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.Message;
import com.cosmin.wsgateway.domain.events.InboundEvent;
import com.cosmin.wsgateway.domain.events.UserMessage;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor(staticName = "newInstance")
public class TransformMessageStage implements Function<Flux<Message>, Flux<InboundEvent>> {
    private final PayloadTransformer transformer;
    private final String connectionId;

    @Override
    public Flux<InboundEvent> apply(Flux<Message> messageFlux) {
        return messageFlux.filter(Predicate.not(Message::isHeartbeat))
                .map(Message::getPayload)
                .map(msg -> new UserMessage(connectionId, transformer.toPayload(msg, Map.class), msg))
                .ofType(InboundEvent.class);
    }
}
