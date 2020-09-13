package com.cosmin.wsgateway.application.gateway.pipeline;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import com.cosmin.wsgateway.application.gateway.connection.Message;
import com.cosmin.wsgateway.application.gateway.connector.ConnectorResolver;
import com.cosmin.wsgateway.domain.events.InboundEvent;
import com.cosmin.wsgateway.domain.events.OutboundEvent;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class StagesProvider {
    private final PayloadTransformer transformer;
    private final ConnectorResolver connectorResolver;
    private final GatewayMetrics gatewayMetrics;

    public Function<Flux<InboundEvent>, Flux<InboundEvent>> appendConnectionLifecyleEventsStage(String connectionId) {
        return AppendConnectionLifecycleEventsStage.newInstance(connectionId);
    }

    public Function<Flux<Message>, Flux<InboundEvent>> transformMessageStage(String connectionId) {
        return TransformMessageStage.newInstance(transformer, connectionId);
    }

    public Function<Flux<InboundEvent>, Flux<OutboundEvent>> sendEventToBackendsStage(Connection.Context context) {
        return SendEventToBackendsStage.newInstance(connectorResolver, context, gatewayMetrics);
    }

    public Function<Flux<OutboundEvent>, Flux<Message>> sendEventToUserStage(Connection.Context context) {
        return SendEventToUserStage.newInstance(transformer, gatewayMetrics, context);
    }

    public Function<Flux<String>, Flux<OutboundEvent>> transformReceivedOutboundEventsStage(
            Connection.Context context
    ) {
        return TransformReceivedOutboundEventsStage.newInstance(transformer, gatewayMetrics, context);
    }

}
