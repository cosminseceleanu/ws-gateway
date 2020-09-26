package com.cosmin.wsgateway.application.gateway.connection;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.PubSub;
import com.cosmin.wsgateway.application.gateway.pipeline.operators.Operators;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.Event;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final Operators operators;
    private final GatewayMetrics gatewayMetrics;

    private static final String OUTBOUND_TOPIC_PATTERN = "inbound.%s";

    private final LongAdder missedPings = new LongAdder();

    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    public static String getOutboundTopic(String connectionId) {
        return String.format(OUTBOUND_TOPIC_PATTERN, connectionId);
    }

    public String getId() {
        return context.getConnectionId();
    }

    public Endpoint getEndpoint() {
        return context.getEndpoint();
    }

    public Long getMissedPings() {
        return missedPings.longValue();
    }

    public boolean isClosed() {
        return isClosed.get();
    }

    public Flux<Message> handle(Flux<Message> messages) {
        logTraceIfEnabled(() -> {
            log.trace("Subscribe for connection {} topic={}",
                    keyValue("connectionId", getId()), context.getOutboundTopic());
        });

        var subscription = pubSub.subscribe(context.getOutboundTopic());

        return messages
                .publishOn(context.getScheduler())
                .doFinally(s -> onConnectionClosed(s, subscription))
                .transform(resetHeartbeatOperator())
                .doOnNext(msg -> missedPings.reset())
                .transform(processMessagesOperator(subscription.getEvents()))
                .transform(heartbeatOperator(messages))
                .onErrorContinue(this::onError);
    }

    private void onConnectionClosed(SignalType signalType, PubSub.Subscription subscription) {
        logTraceIfEnabled(() -> {
            log.trace("Unsubscribe for connection {} signalType={}", keyValue("connectionId", getId()), signalType);
        });
        pubSub.unsubscribe(subscription);
        logTraceIfEnabled(() -> {
            log.trace("Mark connection {} as closed on signalType={}", keyValue("connectionId", getId()), signalType);
        });
        isClosed.compareAndSet(false, true);
    }

    private Function<Flux<Message>, Flux<Message>> resetHeartbeatOperator() {
        return flux -> flux
                .doOnNext(m -> logTraceIfEnabled(() -> log.trace("Reset heartbeat on msg={}", m)))
                .doOnNext(m -> missedPings.reset())
                .filter(Predicate.not(Message::isHeartbeat));
    }

    private void onError(Throwable e, Object o) {
        gatewayMetrics.recordError(e, context.getConnectionId());
        log.error(e.getMessage(), e);
    }

    private void logTraceIfEnabled(Runnable doLog) {
        if (log.isTraceEnabled()) {
            doLog.run();
        }
    }

    private Function<Flux<Message>, Flux<Message>> processMessagesOperator(Flux<String> outboundFlux) {
        Flux<Event> outbound = outboundFlux.transform(operators.outboundFlow(context));

        return flux -> flux
                .transform(operators.inboundFlow(context))
                .mergeWith(outbound)
                .transform(operators.sendToUser(context));
    }

    private Function<Flux<Message>, Flux<Message>> heartbeatOperator(Flux<Message> inbound) {
        var heartbeatFlux = Flux.interval(Duration.ofSeconds(getEndpoint().getHeartbeatIntervalInSeconds()))
                .map(l -> Message.ping())
                .takeUntil(m -> isClosed.get())
                .filter(m -> !isClosed.get())
                .doOnNext(m -> logTraceIfEnabled(() -> log.trace("Send ping message")))
                .doOnNext(msg -> missedPings.increment())
                .map(this::checkHeartbeat)
                .doFinally(signalType -> logTraceIfEnabled(() -> {
                    log.trace("Heartbeat terminated on signalType={}", signalType);
                }));

        return flux -> flux.mergeWith(heartbeatFlux);
    }

    private Message checkHeartbeat(Message msg) {
        if (shouldKeepAliveConnection()) {
            return msg;
        }
        log.info("Connection {} missed {} pings and it will be closed",
                keyValue("connectionId", getId()),
                missedPings.intValue()
        );
        isClosed.compareAndSet(false, true);
        return Message.poisonPill("Missed heartbeats");
    }

    private boolean shouldKeepAliveConnection() {
        return missedPings.intValue() <= getEndpoint().getHeartbeatMaxMissingPingFrames();
    }

    @Getter
    @RequiredArgsConstructor(staticName = "newInstance")
    public static class Context {
        private final String connectionId;
        private final Endpoint endpoint;

        private final Scheduler scheduler = Schedulers.newBoundedElastic(
                Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE,
                Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE,
                GATEWAY_POOL_THREAD_NAME
        );

        public String getOutboundTopic() {
            return Connection.getOutboundTopic(connectionId);
        }
    }
}
