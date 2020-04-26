package com.cosmin.wsgateway.application.gateway.connection;

import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.PubSub;
import com.cosmin.wsgateway.domain.Event;
import com.cosmin.wsgateway.domain.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class Connection {
    private final PubSub pubSub;
    private final PayloadTransformer transformer;
    private final ConnectionContext context;
    private static final String INBOUND_TOPIC_PATTERN = "inbound.%s";

    public Flux<String> handle(Flux<String> inbound) {
        var receivedOutboundEvents = pubSub.subscribe(String.format(INBOUND_TOPIC_PATTERN, context.getId()))
                .map(msg -> new TopicMessage(context.getId(), transformer.toPayload(msg, Map.class)));

        return inbound
                .map(msg -> new WSMessage(context.getId(), transformer.toPayload(msg, Map.class)))
                .ofType(Event.class)
                .startWith(new Connected(context.getId()))
                .concatWithValues(new Disconnected(context.getId()))
                // @ToDo dispatch to backends
                .mergeWith(receivedOutboundEvents)
//                .map(e -> new OutboundEvent(connection.getId(), "ack for " + e.payload().toString()))åå
                .doOnNext(e -> log.debug("event={} of type={} was successfully processed", e.toString(), e.getClass().getName()))
                .onErrorContinue(Exception.class, (e, o) -> log.error(e.getMessage(), e))
                .ofType(OutboundEvent.class)
                .map(e -> transformer.fromPayload(e.payload()));
    }

    public String getId() {
        return context.getId();
    }
}
