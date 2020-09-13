package com.cosmin.wsgateway.application.gateway.pipeline;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import com.cosmin.wsgateway.application.gateway.connection.Message;
import com.cosmin.wsgateway.domain.events.OutboundEvent;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor(staticName = "newInstance")
public class SendEventToUserStage implements Function<Flux<OutboundEvent>, Flux<Message>> {
    private final PayloadTransformer transformer;
    private final GatewayMetrics gatewayMetrics;
    private final Connection.Context context;

    @Override
    public Flux<Message> apply(Flux<OutboundEvent> outboundEventFlux) {
        return outboundEventFlux.map(e -> transformer.fromPayload(e.payload()))
                .map(Message::text)
                .doOnNext(msg -> gatewayMetrics.recordOutboundEventSent(context.getEndpoint(), context.getId()));
    }
}
