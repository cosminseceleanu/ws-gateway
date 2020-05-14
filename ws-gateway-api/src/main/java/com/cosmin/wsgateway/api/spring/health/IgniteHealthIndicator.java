package com.cosmin.wsgateway.api.spring.health;

import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterNode;
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
        var clusterNodes = cluster.nodes()
                .stream()
                .map(ClusterNode::id)
                .map(UUID::toString)
                .collect(Collectors.toSet());
        builder.withDetail("nodes", clusterNodes);
        builder.withDetail("localNode", cluster.localNode().id());
    }
}
