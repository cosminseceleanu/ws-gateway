package com.cosmin.wsgateway.infrastructure.integrations.mock;

import com.cosmin.wsgateway.application.gateway.PubSub;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "gateway", name = "pubsub.mocked")
public class MockedPubSub implements PubSub {
    @Override
    public Subscription subscribe(String topic) {
        log.info("subscribed to {}", StructuredArguments.keyValue("topic", topic));

        var events = Flux.interval(Duration.ofSeconds(50))
                .map(pulse -> "{\"random\":\"" + UUID.randomUUID().toString() + "\"}");

        return new Subscription(topic, events);
    }

    @Override
    public Mono<Void> publish(String topic, String msg) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> unsubscribe(Subscription subscription) {
        log.info("unsubscribed from {}", StructuredArguments.keyValue("subscription", subscription.getId()));
        return Mono.empty();
    }
}
