package com.cosmin.wsgateway.infrastructure.integrations.ignite;

import com.cosmin.wsgateway.application.gateway.PubSub;
import java.io.Serializable;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteMessaging;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
class IgnitePubSub implements PubSub {
    private final IgniteMessaging igniteMessaging;

    @Override
    public Subscription subscribe(String topic) {
        var processor = new EventProcessor();

        Flux<String> events = Flux.create(sink -> processor.setListener(sink::next));

        var subscriptionId = igniteMessaging.remoteListen(topic, (nodeId, msg) -> {
            if (log.isTraceEnabled()) {
                log.trace("Received event message [msg={}, from={}]", msg, nodeId);
            }
            if (msg instanceof String) {
                processor.onMessage((String) msg);
            } else if (log.isDebugEnabled()) {
                log.debug("Drop ignite message [msg={}, from={}]", msg, nodeId);
            }

            return true; // Return true to continue listening.
        });

        return new Subscription(subscriptionId.toString(), events);
    }

    @Override
    public Mono<Void> publish(String topic, String msg) {
        return Mono.fromRunnable(() -> {
            igniteMessaging.send(topic, msg);
        });
    }

    @Override
    public Mono<Void> unsubscribe(Subscription subscription) {
        return Mono.fromRunnable(() -> {
            igniteMessaging.stopRemoteListen(UUID.fromString(subscription.getId()));
        });
    }

    public static class EventProcessor implements Serializable {
        private EventListener listener;

        void onMessage(String message) {
            if (listener != null) {
                listener.onMessage(message);
            }
        }

        public void setListener(EventListener listener) {
            this.listener = listener;
        }
    }

    public interface EventListener {
        void onMessage(String message);
    }
}
