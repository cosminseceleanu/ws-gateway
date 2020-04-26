package com.cosmin.infrastructure.gateway;

import com.cosmin.wsgateway.application.gateway.PubSub;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;

@Slf4j
public class MockedPubSub implements PubSub {
    @Override
    public Flux<String> subscribe(String topic) {
        log.info("subscribed to topic={}", topic);

        return Flux.interval(Duration.ofSeconds(10))
                .map(pulse -> "{\"random\":\"" + UUID.randomUUID().toString() + "\"}");

    }
}
