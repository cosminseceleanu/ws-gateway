package com.cosmin.wsgateway.application.gateway.connection;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PubSub;
import com.cosmin.wsgateway.application.gateway.pipeline.StagesProvider;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.events.OutboundEvent;
import java.time.Duration;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;


@Slf4j
@RequiredArgsConstructor
public class Connection {
    private static final String GATEWAY_POOL_THREAD_NAME = "gateway-thread";

    private final PubSub pubSub;
    private final Context context;
    private final GatewayMetrics gatewayMetrics;
    private final StagesProvider stagesProvider;

    private static final String OUTBOUND_TOPIC_PATTERN = "inbound.%s";

    private final LongAdder missedPings = new LongAdder();

    private Disposable heartbeatDisposable;

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

    public Flux<Message> handle(Flux<Message> inbound) {
        var subscription = pubSub.subscribe(String.format(OUTBOUND_TOPIC_PATTERN, context.getId()));

        return inbound
                .doOnNext(msg -> missedPings.reset())
                .transform(stagesProvider.transformMessageStage(context.getId()))
                .transform(stagesProvider.appendConnectionLifecyleEventsStage(context.getId()))
                .doFinally(s -> onFinally(s, subscription))
                .publishOn(scheduler)
                .transform(stagesProvider.sendEventToBackendsStage(context))
                .transform(handleOutbound(subscription))
                .transform(sendPing());
    }

    private void onFinally(SignalType signalType, PubSub.Subscription subscription) {
        log.debug("Unsubscribe and close heartbeat for connection {} signalType={}",
                keyValue("connectionId", getId()),
                signalType
        );
        pubSub.unsubscribe(subscription);
        heartbeatDisposable.dispose();
    }

    private Function<Flux<OutboundEvent>, Flux<Message>> handleOutbound(PubSub.Subscription subscription) {
        var receivedOutboundEvents = subscription.getEvents()
                .publishOn(scheduler)
                .transform(stagesProvider.transformReceivedOutboundEventsStage(context));

        return flux -> flux.mergeWith(receivedOutboundEvents)
                .doOnNext(this::logProcessedEvent)
                .onErrorContinue(Exception.class, this::onError)
                .transform(stagesProvider.sendEventToUserStage(context));
    }

    private void logProcessedEvent(OutboundEvent e) {
        if (log.isDebugEnabled()) {
            log.debug("{} of {} was successfully processed",
                    keyValue("event", e.toString()),
                    keyValue("type", e.getClass().getName())
            );
        }
    }

    private Function<Flux<Message>, Flux<Message>> sendPing() {
        Flux<Message> heartbeatFlux = Flux.interval(Duration.ofSeconds(getEndpoint().getHeartbeatIntervalInSeconds()))
                .map(l -> Message.ping())
                .doOnNext(msg -> missedPings.increment())
                .map(this::checkHeartbeat)
                .doFinally(signalType -> log.info("heartbeat terminated signalType={}", signalType));
        heartbeatDisposable = heartbeatFlux.subscribe();

        return flux -> flux.mergeWith(heartbeatFlux);
    }

    private Message checkHeartbeat(Message msg) {
        if (missedPings.intValue() <= getEndpoint().getHeartbeatMaxMissingPingFrames()) {
            return msg;
        }
        log.info("Connection {} missed {} pings and it will be closed",
                keyValue("connectionId", getId()),
                missedPings.intValue()
        );
        return Message.poisonPill("Missed heartbeats");
    }

    private void onError(Throwable e, Object o) {
        gatewayMetrics.recordError(e, context.getId());
        log.error(e.getMessage(), e);
    }

    @Getter
    @RequiredArgsConstructor(staticName = "newInstance")
    public static class Context {
        private final String id;
        private final Endpoint endpoint;
    }
}
