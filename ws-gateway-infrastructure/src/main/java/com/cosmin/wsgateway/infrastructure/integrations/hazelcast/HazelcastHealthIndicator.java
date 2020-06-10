package com.cosmin.wsgateway.infrastructure.integrations.hazelcast;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

@RequiredArgsConstructor
class HazelcastHealthIndicator extends AbstractHealthIndicator {
    private final HazelcastInstance hazelcastInstance;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        var cluster = hazelcastInstance.getCluster();
        if (hazelcastInstance.getLifecycleService().isRunning()) {
            builder.up();
        } else {
            builder.down();
        }
        var clusterNodes = cluster.getMembers()
                .stream()
                .filter(member -> !member.localMember())
                .map(Member::getUuid)
                .map(UUID::toString)
                .collect(Collectors.toSet());

        builder.withDetail("localNode", cluster.getLocalMember().getUuid().toString())
                .withDetail("nodes", clusterNodes);
    }
}
