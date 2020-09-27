package com.cosmin.wsgateway.application.gateway;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PubSub {

    Subscription subscribe(String topic);

    Mono<Void> publish(String topic, String msg);

    Mono<Void> unsubscribe(Subscription subscription);

    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    class Subscription {
        private final String id;
        private final Flux<String> events;
    }
}
