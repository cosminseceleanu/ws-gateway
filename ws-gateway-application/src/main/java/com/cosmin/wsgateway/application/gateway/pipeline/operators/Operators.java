package com.cosmin.wsgateway.application.gateway.pipeline.operators;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import com.cosmin.wsgateway.application.gateway.connection.Message;
import com.cosmin.wsgateway.application.gateway.connector.ConnectorResolver;
import com.cosmin.wsgateway.domain.Event;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class Operators {
    private final GatewayMetrics gatewayMetrics;
    private final PayloadTransformer payloadTransformer;
    private final ConnectorResolver connectorResolver;

    public Function<Flux<Message>, Flux<Event>> inboundFlow(Connection.Context context) {
        return new InboundFlowOperator(sendToBackends(context), payloadTransformer, context);
    }

    public Function<Flux<Event>, Flux<Event>> sendToBackends(Connection.Context context) {
        return new SendToBackendsOperator(connectorResolver, context, gatewayMetrics);
    }

    public Function<Flux<String>, Flux<Event>> outboundFlow(Connection.Context context) {
        return new OutboundFlowOperator(payloadTransformer, gatewayMetrics, context);
    }

    public Function<Flux<Event>, Flux<Message>> sendToUser(Connection.Context context) {
        return new SendToUserOperator(payloadTransformer, gatewayMetrics, context);
    }

    public static Function<Flux<Event>, Flux<Event>> logEventOperator() {
        return flux -> flux.doOnNext(e -> {
            if (log.isDebugEnabled()) {
                log.debug("{} of {} was successfully processed",
                        keyValue("event", e.toString()),
                        keyValue("type", e.getClass().getName())
                );
            }
        });
    }
}
