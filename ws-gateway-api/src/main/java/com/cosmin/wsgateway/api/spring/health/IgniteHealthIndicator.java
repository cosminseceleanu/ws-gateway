package com.cosmin.wsgateway.api.spring.health;

import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class IgniteHealthIndicator extends AbstractHealthIndicator {
    private final Ignite ignite;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        var cluster = ignite.cluster();
        if (cluster.active()) {
            builder.up();
        } else {
            builder.down();
        }
        builder.withDetail("nodes", cluster.nodeLocalMap());
        builder.withDetail("localNode", cluster.localNode().id());
    }
}
