package com.cosmin.wsgateway.application.gateway.pipeline;

import static com.cosmin.wsgateway.application.gateway.GatewayMetrics.METRIC_INBOUND_EVENTS;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import com.cosmin.wsgateway.application.gateway.connection.events.BackendErrorEvent;
import com.cosmin.wsgateway.application.gateway.connector.BackendConnector;
import com.cosmin.wsgateway.application.gateway.connector.ConnectorResolver;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.Event;
import com.cosmin.wsgateway.domain.events.InboundEvent;
import com.cosmin.wsgateway.domain.events.OutboundEvent;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(staticName = "newInstance")
@Slf4j
public class SendEventToBackendsStage implements Function<Flux<InboundEvent>, Flux<OutboundEvent>> {
    private final ConnectorResolver connectorResolver;
    private final Connection.Context context;
    private final GatewayMetrics gatewayMetrics;

    @Override
    public Flux<OutboundEvent> apply(Flux<InboundEvent> flux) {
        return flux
                .map(e -> Pair.with(e, gatewayMetrics.startTimer(METRIC_INBOUND_EVENTS, context.getEndpoint())))
                .flatMap(pair -> sendInboundEventToBackends(pair.getValue0()).map(e -> Pair.with(e, pair.getValue1())))
                .doOnNext(pair -> pair.getValue1().stop())
                .map(Pair::getValue0)
                .ofType(OutboundEvent.class);
    }

    private Flux<? extends Event> sendInboundEventToBackends(InboundEvent inboundEvent) {
        var backends = inboundEvent.getRoute(context.getEndpoint()).getBackends();
        log.debug("send {} to {}", keyValue("event", inboundEvent), keyValue("backends", backends));

        Integer parallelism = context.getEndpoint().getGeneralSettings().getBackendParallelism();

        return Flux.fromIterable(backends)
                .map(b -> BackendConnectorPair.of(b, connectorResolver.getConnector(b)))
                .flatMap(pair -> sendInboundEventToBackend(inboundEvent, pair), parallelism);
    }

    private Mono<Event> sendInboundEventToBackend(InboundEvent inboundEvent, BackendConnectorPair pair) {
        return gatewayMetrics.measureMono(
                GatewayMetrics.METRIC_INBOUND_BACKENDS,
                pair.doSendEvent(inboundEvent),
                context.getEndpoint(),
                Map.of("backend.type", pair.backend.type().name(), "backend.destination", pair.backend.destination())
        )
                .doOnError(e -> gatewayMetrics.recordBackendError(context.getEndpoint(), pair.backend, context.getId()))
                .onErrorResume(e -> Mono.just(BackendErrorEvent.of(inboundEvent, e)));
    }

    @RequiredArgsConstructor
    private static class BackendConnectorPair {
        private final Backend<BackendSettings> backend;
        private final BackendConnector<BackendSettings> connector;

        public static BackendConnectorPair of(
                Backend<? extends BackendSettings> b, BackendConnector<? extends BackendSettings> connector
        ) {
            return new BackendConnectorPair(
                    (Backend<BackendSettings>) b, (BackendConnector<BackendSettings>) connector
            );
        }

        Mono<Event> doSendEvent(InboundEvent inboundEvent) {
            return connector.sendEvent(inboundEvent, backend);
        }
    }
}
