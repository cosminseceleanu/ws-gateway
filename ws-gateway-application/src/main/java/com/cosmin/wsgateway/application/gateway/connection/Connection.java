package com.cosmin.wsgateway.application.gateway.connection;

import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.PubSub;
import com.cosmin.wsgateway.application.gateway.connector.BackendConnector;
import com.cosmin.wsgateway.application.gateway.connector.ConnectorResolver;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
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
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;


@Slf4j
@RequiredArgsConstructor
public class Connection {
    //@ToDo set this value per endpoint
    private static final int DEFAULT_BACKEND_CONCURRENCY = 8;

    private final PubSub pubSub;
    private final PayloadTransformer transformer;
    private final ConnectorResolver connectorResolver;
    private final ConnectionContext context;

    //@ToDo make it configurable
    private static final String INBOUND_TOPIC_PATTERN = "inbound.%s";

    private final Scheduler scheduler = Schedulers.newBoundedElastic(
            Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE,
            Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE,
            "gateway-thread"
    );

    public String getId() {
        return context.getId();
    }

    public Flux<String> handle(Flux<String> inbound) {
        var receivedOutboundEvents = pubSub.subscribe(String.format(INBOUND_TOPIC_PATTERN, context.getId()))
                .map(msg -> new TopicMessage(context.getId(), transformer.toPayload(msg, Map.class)));

        return getInboundEventFlux(inbound)
                .publishOn(scheduler)
                .flatMap(this::sendEventToBackends)
                .ofType(OutboundEvent.class)
                .mergeWith(receivedOutboundEvents)
                .doOnNext(e -> {
                    log.debug("event={} of type={} was successfully processed", e.toString(), e.getClass().getName());
                })
                .onErrorContinue(Exception.class, (e, o) -> log.error(e.getMessage(), e))
                .map(e -> transformer.fromPayload(e.payload()));
    }

    private Flux<InboundEvent> getInboundEventFlux(Flux<String> inbound) {
        return inbound
                .map(msg -> new UserMessage(context.getId(), transformer.toPayload(msg, Map.class), msg))
                .ofType(InboundEvent.class)
                .startWith(new Connected(context.getId()))
                .concatWithValues(new Disconnected(context.getId()));
    }

    private Publisher<? extends Event> sendEventToBackends(InboundEvent inboundEvent) {
        var backends = inboundEvent.getRoute(context.getEndpoint()).getBackends();
        log.debug("send event={} to backends={}", inboundEvent, backends);

        return Flux.fromIterable(backends)
                .map(b -> BackendConnectorPair.of(b, connectorResolver.getConnector(b)))
                .flatMap(pair -> pair.doSendEvent(inboundEvent), DEFAULT_BACKEND_CONCURRENCY);
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
