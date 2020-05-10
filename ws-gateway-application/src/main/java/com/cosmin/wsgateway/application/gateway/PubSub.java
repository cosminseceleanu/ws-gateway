package com.cosmin.wsgateway.application.gateway;

import lombok.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PubSub {

    Subscription subscribe(String topic);

    Mono<Void> publish(String topic, String msg);

    Mono<Void> unsubscribe(Subscription subscription);

    @Value
    class Subscription {
        private final String id;
        private final Flux<String> events;
    }
}
