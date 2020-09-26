package com.cosmin.wsgateway.application.gateway.pipeline.operators;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import com.cosmin.wsgateway.domain.Event;
import com.cosmin.wsgateway.domain.events.TopicMessage;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;


@RequiredArgsConstructor
@Slf4j
public class OutboundFlowOperator implements Function<Flux<String>, Flux<Event>> {
    private final PayloadTransformer transformer;
    private final GatewayMetrics gatewayMetrics;
    private final Connection.Context context;

    @Override
    public Flux<Event> apply(Flux<String> flux) {
        return flux
                .publishOn(context.getScheduler())
                .transform(toOutboundEventOperator())
                .transform(Operators.logEventOperator())
                .transform(onErrorContinueOperator());
    }

    private Function<Flux<String>, Flux<Event>> toOutboundEventOperator() {
        return flux -> flux
                .doOnNext(n -> {
                    gatewayMetrics.recordOutboundEventReceived(context.getEndpoint(), context.getConnectionId());
                })
                .map(msg -> new TopicMessage(context.getConnectionId(), transformer.toPayload(msg, Object.class)));
    }

    private <T extends Event> Function<Flux<T>, Flux<T>> onErrorContinueOperator() {
        return flux -> flux.onErrorContinue(Exception.class, (e, o) -> {
            gatewayMetrics.recordError(e, context.getConnectionId());
            log.error(e.getMessage(), e);
        });
    }
}
