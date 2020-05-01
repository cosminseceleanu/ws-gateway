package com.cosmin.wsgateway.infrastructure.gateway;

import com.cosmin.wsgateway.application.gateway.PubSub;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class MockedPubSub implements PubSub {
    @Override
    public Flux<String> subscribe(String topic) {
        log.info("subscribed to topic={}", topic);

        return Flux.interval(Duration.ofSeconds(50))
                .map(pulse -> "{\"random\":\"" + UUID.randomUUID().toString() + "\"}");

    }
}
