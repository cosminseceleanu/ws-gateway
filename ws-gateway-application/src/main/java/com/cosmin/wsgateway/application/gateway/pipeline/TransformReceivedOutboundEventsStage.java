package com.cosmin.wsgateway.application.gateway.pipeline;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import com.cosmin.wsgateway.domain.events.OutboundEvent;
import com.cosmin.wsgateway.domain.events.TopicMessage;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor(staticName = "newInstance")
public class TransformReceivedOutboundEventsStage implements Function<Flux<String>, Flux<OutboundEvent>> {
    private final PayloadTransformer transformer;
    private final GatewayMetrics gatewayMetrics;
    private final Connection.Context context;

    @Override
    public Flux<OutboundEvent> apply(Flux<String> flux) {
        return flux
                .doOnNext(n -> gatewayMetrics.recordOutboundEventReceived(context.getEndpoint(), context.getId()))
                .map(msg -> new TopicMessage(context.getId(), transformer.toPayload(msg, Map.class)));
    }
}
