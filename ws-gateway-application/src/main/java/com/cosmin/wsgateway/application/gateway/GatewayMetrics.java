package com.cosmin.wsgateway.application.gateway;

import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.Endpoint;
import java.util.Collections;
import java.util.Map;
import reactor.core.publisher.Mono;

public interface GatewayMetrics {

    String METRIC_CONNECTION_ERRORS = "gateway.connections.errors";
    String METRIC_ACTIVE_CONNECTIONS = "gateway.active.connections";

    String METRIC_ERRORS = "gateway.errors";
    String METRIC_OUTBOUND_EVENTS_SENT = "gateway.outbound.sent";
    String METRIC_OUTBOUND_EVENTS_RECEIVED = "gateway.outbound.received";

    String METRIC_INBOUND_BACKENDS = "gateway.inbound.backends";
    String METRIC_INBOUND_BACKEND_ERROR = "gateway.inbound.backends.error";
    String METRIC_INBOUND_EVENTS = "gateway.inbound";
    String METRIC_INBOUND_EVENTS_RECEIVED = "gateway.inbound.received";

    void recordConnection(Endpoint endpoint);

    void recordConnectionError(int status);

    void recordError(Throwable e, String connectionId);

    void recordOutboundEventSent(Endpoint endpoint, String connectionId);

    void recordOutboundEventReceived(Endpoint endpoint, String connectionId);

    void recordInboundEventReceived(Endpoint endpoint, String connectionId);

    void recordBackendError(Endpoint endpoint, Backend<?> backend, String connectionId);

    default Stopwatch startTimer(String metricName, Endpoint endpoint) {
        return startTimer(metricName, endpoint, Collections.emptyMap());
    }

    Stopwatch startTimer(String metricName, Endpoint endpoint, Map<String, String> tags);

    default <T> Mono<T> measureMono(String metricName, Mono<T> measured, Endpoint endpoint) {
        return measureMono(metricName, measured, endpoint, Collections.emptyMap());
    }

    <T> Mono<T> measureMono(String metricName, Mono<T> measured, Endpoint endpoint, Map<String, String> tags);

    interface Stopwatch {
        void stop();
    }
}
