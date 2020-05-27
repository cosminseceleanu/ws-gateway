package com.cosmin.wsgateway.application.gateway.connection;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.PubSub;
import com.cosmin.wsgateway.application.gateway.connector.BackendConnector;
import com.cosmin.wsgateway.application.gateway.connector.ConnectorResolver;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.Event;
import com.cosmin.wsgateway.domain.events.Connected;
import com.cosmin.wsgateway.domain.events.Disconnected;
import com.cosmin.wsgateway.domain.events.InboundEvent;
import com.cosmin.wsgateway.domain.events.OutboundEvent;
import com.cosmin.wsgateway.domain.events.TopicMessage;
import com.cosmin.wsgateway.domain.events.UserMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;


@Slf4j
@RequiredArgsConstructor
public class Connection {
    //@ToDo set this value per endpoint
    private static final int DEFAULT_BACKEND_CONCURRENCY = 8;
    private static final String GATEWAY_POOL_THREAD_NAME = "gateway-thread";

    private final PubSub pubSub;
    private final PayloadTransformer transformer;
    private final ConnectorResolver connectorResolver;
    private final ConnectionContext context;
    private final GatewayMetrics gatewayMetrics;

    private static final String OUTBOUND_TOPIC_PATTERN = "inbound.%s";

    private final Scheduler scheduler = Schedulers.newBoundedElastic(
            Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE,
            Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE,
            GATEWAY_POOL_THREAD_NAME
    );

    public static String getOutboundTopic(String connectionId) {
        return String.format(OUTBOUND_TOPIC_PATTERN, connectionId);
    }

    public String getId() {
        return context.getId();
    }

    public Endpoint getEndpoint() {
        return context.getEndpoint();
    }

    public Flux<String> handle(Flux<String> inbound) {
        var subscription = pubSub.subscribe(String.format(OUTBOUND_TOPIC_PATTERN, context.getId()));
        var receivedOutboundEvents = createOutboundFlux(subscription);

        return doHandleInboundEvents(inbound)
                .doFinally(signalType -> pubSub.unsubscribe(subscription))
                .mergeWith(receivedOutboundEvents)
                .doOnNext(this::logProcessedEvent)
                .onErrorContinue(Exception.class, this::onError)
                .map(e -> transformer.fromPayload(e.payload()))
                .doOnNext(msg -> gatewayMetrics.recordOutboundEventSent(context.getEndpoint(), context.getId()));
    }

    private Flux<OutboundEvent> createOutboundFlux(PubSub.Subscription subscription) {
        var outboundEvents = subscription.getEvents();

        return outboundEvents
                .doOnNext(n -> gatewayMetrics.recordOutboundEventReceived(context.getEndpoint(), context.getId()))
                .map(msg -> new TopicMessage(context.getId(), transformer.toPayload(msg, Map.class)));
    }

    private Flux<OutboundEvent> doHandleInboundEvents(Flux<String> inbound) {
        Flux<InboundEvent> inboundEvents = inbound
                .map(msg -> new UserMessage(context.getId(), transformer.toPayload(msg, Map.class), msg))
                .ofType(InboundEvent.class)
                .startWith(new Connected(context.getId()))
                .concatWithValues(new Disconnected(context.getId()));

        return inboundEvents
                .publishOn(scheduler)
                .map(e -> Pair.with(e, gatewayMetrics.startTimer(GatewayMetrics.METRIC_INBOUND_EVENTS, getEndpoint())))
                .flatMap(pair -> sendInboundEventToBackends(pair.getValue0()).map(e -> Pair.with(e, pair.getValue1())))
                .doOnNext(pair -> pair.getValue1().stop())
                .map(Pair::getValue0)
                .ofType(OutboundEvent.class);

    }

    private Flux<? extends Event> sendInboundEventToBackends(InboundEvent inboundEvent) {
        var backends = inboundEvent.getRoute(context.getEndpoint()).getBackends();
        log.debug("send event={} to backends={}", inboundEvent, backends);

        return Flux.fromIterable(backends)
                .map(b -> BackendConnectorPair.of(b, connectorResolver.getConnector(b)))
                .flatMap(pair -> sendInboundEventToBackend(inboundEvent, pair), DEFAULT_BACKEND_CONCURRENCY);
    }

    private Mono<Event> sendInboundEventToBackend(InboundEvent inboundEvent, Connection.BackendConnectorPair pair) {
        return gatewayMetrics.measureMono(
                GatewayMetrics.METRIC_INBOUND_BACKENDS,
                pair.doSendEvent(inboundEvent),
                getEndpoint(),
                Map.of("backend.type", pair.backend.type().name(), "backend.destination", pair.backend.destination())
        ).doOnError(e -> gatewayMetrics.recordBackendError(getEndpoint(), pair.backend, context.getId()));
    }

    private void logProcessedEvent(OutboundEvent e) {
        if (log.isDebugEnabled()) {
            log.debug("event={} of type={} was successfully processed", e.toString(), e.getClass().getName());
        }
    }

    private void onError(Throwable e, Object o) {
        gatewayMetrics.recordError(e, context.getId());
        log.error(e.getMessage(), e);
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
