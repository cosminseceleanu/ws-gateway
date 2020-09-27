package com.cosmin.wsgateway.application.gateway.pipeline.operators;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import com.cosmin.wsgateway.application.gateway.connection.Message;
import com.cosmin.wsgateway.domain.Event;
import com.cosmin.wsgateway.domain.events.OutboundEvent;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;


@RequiredArgsConstructor
public class SendToUserOperator implements Function<Flux<Event>, Flux<Message>> {
    private final PayloadTransformer transformer;
    private final GatewayMetrics gatewayMetrics;
    private final Connection.Context context;

    @Override
    public Flux<Message> apply(Flux<Event> eventFlux) {
        return eventFlux
            .ofType(OutboundEvent.class)
            .map(e -> transformer.fromPayload(e.payload()))
            .map(Message::text)
            .doOnNext(msg -> gatewayMetrics.recordOutboundEventSent(context.getEndpoint(), context.getConnectionId()));
    }
}
