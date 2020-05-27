package com.cosmin.wsgateway.infrastructure.integrations.ignite;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterMetrics;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class IgniteMetrics {
    public static final String METRIC_NAME = "apache.ignite";
    private final Ignite ignite;
    private final MeterRegistry meterRegistry;

    @Scheduled(fixedRate = 15000)
    public void dumpMetrics() {
        ClusterMetrics metrics = ignite.cluster().localNode().metrics();
        meterRegistry.gauge(METRIC_NAME, Tags.of("metric","receivedMessagesCount"), metrics.getReceivedMessagesCount());
        meterRegistry.gauge(METRIC_NAME, Tags.of("metric","sentMessagesCount"), metrics.getSentMessagesCount());
        meterRegistry.gauge(METRIC_NAME, Tags.of("metric","outboundMessagesQueueSize"),
                metrics.getOutboundMessagesQueueSize());
        meterRegistry.gauge(METRIC_NAME, Tags.of("metric","totalNodes"), metrics.getTotalNodes());
    }
}
