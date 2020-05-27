package com.cosmin.wsgateway.infrastructure.gateway.metrics;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.Endpoint;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MicrometerMetrics implements GatewayMetrics {
    private final MeterRegistry registry;
    private final AtomicLong activeConnectionsGauge;

    public MicrometerMetrics(MeterRegistry registry) {
        this.registry = registry;
        activeConnectionsGauge = registry.gauge(GatewayMetrics.METRIC_ACTIVE_CONNECTIONS, new AtomicLong(0));
    }

    @Override
    public void recordConnection(Endpoint endpoint) {
        activeConnectionsGauge.getAndIncrement();
    }

    @Override
    public void recordConnectionError(int status) {
        List<Tag> tags = List.of(Tag.of("response.status", String.valueOf(status)));
        registry.counter(GatewayMetrics.METRIC_CONNECTION_ERRORS, tags).increment();
    }

    @Override
    public void recordError(Throwable e) {
        List<Tag> tags = List.of(Tag.of("error.type", e.getClass().getSimpleName()));
        registry.counter(GatewayMetrics.METRIC_ERRORS, tags).increment();
    }

    @Override
    public void recordOutboundEventSent(Endpoint endpoint) {
        registry.counter(GatewayMetrics.METRIC_OUTBOUND_EVENTS_SENT, getEndpointTags(endpoint)).increment();
    }

    @Override
    public void recordOutboundEventReceived(Endpoint endpoint) {
        registry.counter(GatewayMetrics.METRIC_OUTBOUND_EVENTS_RECEIVED, getEndpointTags(endpoint)).increment();
    }

    @Override
    public void recordInboundEventReceived(Endpoint endpoint) {
        registry.counter(GatewayMetrics. METRIC_INBOUND_EVENTS_RECEIVED, getEndpointTags(endpoint)).increment();
    }

    @Override
    public void recordBackendError(Endpoint endpoint, Backend<?> backend) {
        var tags = List.of(
                Tag.of("endpoint.path", String.valueOf(endpoint.getPath())),
                Tag.of("backend.type", backend.type().name()),
                Tag.of("backend.destination", backend.destination())
        );
        registry.counter(GatewayMetrics.METRIC_INBOUND_BACKEND_ERROR, tags).increment();
    }

    private List<Tag> getEndpointTags(Endpoint endpoint) {
        return List.of(Tag.of("endpoint.path", String.valueOf(endpoint.getPath())));
    }

    @Override
    public Stopwatch startTimer(String metricName, Endpoint endpoint, Map<String, String> tags) {
        Timer timer = createTimer(metricName, endpoint, tags);

        return new MicrometerStopwatch(Timer.start(registry), timer);
    }

    @Override
    public <T> Mono<T> measureMono(String metricName, Mono<T> measured, Endpoint endpoint, Map<String, String> tags) {
        Timer timer = createTimer(metricName, endpoint, tags);

        return Mono.just(Timer.start(registry))
                .flatMap(sample -> measured.doFinally(signalType -> sample.stop(timer)));
    }

    private Timer createTimer(String metricName, Endpoint endpoint, Map<String, String> tags) {
        return Timer.builder(metricName)
                .tags(toIterableTags(tags))
                .tag("endpoint.path", endpoint.getPath())
                .publishPercentiles(0.95, 0.99)
                .publishPercentileHistogram(true)
                .register(registry);
    }

    private Iterable<Tag> toIterableTags(Map<String, String> tags) {
        return tags.entrySet()
                .stream()
                .map(e -> Tag.of(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Data
    private static class MicrometerStopwatch implements Stopwatch {
        private final Timer.Sample sample;
        private final Timer timer;

        @Override
        public void stop() {
            sample.stop(timer);
        }
    }
}
