package com.cosmin.wsgateway.api.spring.filters;

import com.cosmin.wsgateway.api.utils.HttpLogger;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.sleuth.instrument.web.TraceWebFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements WebFilter, Ordered {

    private static final Set<MediaType> ALLOWED_MEDIA_TYPES = Set.of(
            MediaType.TEXT_XML,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_JSON_UTF8,
            MediaType.TEXT_PLAIN,
            MediaType.TEXT_HTML,
            MediaType.TEXT_MARKDOWN
    );


    @Override
    public int getOrder() {
        return TraceWebFilter.ORDER + 10;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.subscriberContext()
                .flatMap(context -> {
                    HttpLogger.logServerRequest(exchange.getRequest());

                    return chain.filter(new ExchangeDecorator(
                            exchange,
                            new HttpResponseDecorator(exchange.getResponse(), exchange.getRequest())
                    ));
                });
    }

    private class ExchangeDecorator extends ServerWebExchangeDecorator {
        private final HttpResponseDecorator httpResponseDecorator;

        public ExchangeDecorator(ServerWebExchange delegate, HttpResponseDecorator httpResponseDecorator) {
            super(delegate);
            this.httpResponseDecorator = httpResponseDecorator;
        }

        @Override
        public ServerHttpResponse getResponse() {
            return httpResponseDecorator;
        }
    }

    private static class HttpResponseDecorator extends ServerHttpResponseDecorator {
        private final ServerHttpRequest request;

        HttpResponseDecorator(ServerHttpResponse delegate, ServerHttpRequest request) {
            super(delegate);
            this.request = request;
        }

        @Override
        public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
            return super.writeAndFlushWith(body);
        }

        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            if (!log.isDebugEnabled()) {
                return super.writeWith(body);
            }
            final MediaType contentType = super.getHeaders().getContentType();
            if (ALLOWED_MEDIA_TYPES.contains(contentType)) {
                if (body instanceof Mono) {
                    final Mono<DataBuffer> monoBody = (Mono<DataBuffer>) body;
                    return super.writeWith(monoBody.map(b -> HttpLogger.logResponse(b, this, request)));
                } else if (body instanceof Flux) {
                    final Flux<DataBuffer> fluxBody = (Flux<DataBuffer>) body;
                    return super.writeWith(fluxBody.map(b -> HttpLogger.logResponse(b, this, request)));
                }
            }
            return super.writeWith(body);
        }
    }
}
