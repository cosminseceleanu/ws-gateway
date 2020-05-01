package com.cosmin.wsgateway.application.gateway.connection.filters;

import com.cosmin.wsgateway.application.gateway.connection.ConnectionRequest;
import com.cosmin.wsgateway.domain.Endpoint;
import reactor.core.publisher.Mono;

public interface ConnectionFilter {
    Mono<ConnectionRequest> filter(Endpoint endpoint, ConnectionRequest request);
}
