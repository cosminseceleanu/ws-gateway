package com.cosmin.wsgateway.api.spring.filters;

import brave.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.instrument.web.TraceWebFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class TraceIdFilter implements WebFilter, Ordered {

    private static final String X_TRACEID_HEADER = "X-B3-TraceId";
    private final Tracer tracer;

    @Override
    public int getOrder() {
        return TraceWebFilter.ORDER + 8;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.subscriberContext()
                .flatMap(context -> addTracingHeader(exchange, chain));
    }

    private Mono<? extends Void> addTracingHeader(ServerWebExchange exchange, WebFilterChain chain) {
        var result = chain.filter(exchange);
        var span = tracer.currentSpan();
        if (span == null) {
            return result;
        }
        exchange.getResponse().getHeaders().add(X_TRACEID_HEADER, span.context().traceIdString());

        return result;
    }
}