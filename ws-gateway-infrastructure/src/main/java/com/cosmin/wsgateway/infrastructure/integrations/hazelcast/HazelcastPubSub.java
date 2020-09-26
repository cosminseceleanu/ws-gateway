package com.cosmin.wsgateway.infrastructure.integrations.hazelcast;

import com.cosmin.wsgateway.application.gateway.PubSub;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
class HazelcastPubSub implements PubSub {
    private final HazelcastInstance hazelcastInstance;
    private final ConcurrentHashMap<String, String> topicBySubscriptionId = new ConcurrentHashMap<>();

    @Override
    public Subscription subscribe(String topic) {
        var processor = new EventProcessor();
        Flux<String> events = Flux.create(sink -> processor.setListener(sink::next));

        ITopic<String> topicInstance = hazelcastInstance.getTopic(topic);
        var subscriptionId = topicInstance.addMessageListener(msg -> {
            processor.onMessage(msg.getMessageObject());
        }).toString();
        topicBySubscriptionId.put(subscriptionId, topic);

        return new Subscription(subscriptionId, events);
    }

    @Override
    public Mono<Void> publish(String topic, String msg) {
        return Mono.fromRunnable(() -> {
            ITopic<String> topicInstance = hazelcastInstance.getTopic(topic);
            topicInstance.publish(msg);
        });
    }

    @Override
    public Mono<Void> unsubscribe(Subscription subscription) {
        return Mono.fromRunnable(() -> {
            Optional.ofNullable(topicBySubscriptionId.get(subscription.getId()))
                    .map(hazelcastInstance::getTopic)
                    .ifPresent(topic -> {
                        topic.removeMessageListener(UUID.fromString(subscription.getId()));
                        topicBySubscriptionId.remove(subscription.getId());
                    });
        });
    }

    public static class EventProcessor {
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
