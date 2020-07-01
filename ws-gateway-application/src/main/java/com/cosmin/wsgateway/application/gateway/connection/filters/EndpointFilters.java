package com.cosmin.wsgateway.application.gateway.connection.filters;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import com.cosmin.wsgateway.application.gateway.connection.ConnectionRequest;
import com.cosmin.wsgateway.application.gateway.exceptions.AccessDeniedException;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class EndpointFilters implements ConnectionFilter {
    @Override
    public Mono<ConnectionRequest> filter(Endpoint endpoint, ConnectionRequest request) {
        log.debug("check filters {} for {}", endpoint.getFilters(), keyValue("path", endpoint.getPath()));
        for (Filter<?> filter : endpoint.getFilters()) {
            if (!filter.isAllowed(request.getHeaders())) {
                log.info("Filter {} doesnt allow connection for {}",
                        filter.name(),
                        keyValue("headers", request.getHeaders())
                );
                return Mono.error(new AccessDeniedException("Access Denied!"));
            }
        }
        return Mono.just(request);

    }
}
