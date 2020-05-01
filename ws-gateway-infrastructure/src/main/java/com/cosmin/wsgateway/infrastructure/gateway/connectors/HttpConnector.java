package com.cosmin.wsgateway.infrastructure.gateway.connectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.cosmin.wsgateway.application.gateway.connection.events.BackendErrorEvent;
import com.cosmin.wsgateway.application.gateway.connector.BackendConnector;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.Event;
import com.cosmin.wsgateway.domain.backends.HttpSettings;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;


@Component
@RequiredArgsConstructor
@Slf4j
public class HttpConnector implements BackendConnector<HttpSettings> {
    @Override
    public boolean supports(Backend.Type type) {
        return Backend.Type.HTTP.equals(type);
    }

    @Override
    public Mono<Event> sendEvent(Event event, Backend<HttpSettings> backend) {
        return prepareRequest(backend)
                .body(Mono.just(event.payload()), Object.class)
                .exchange()
                .doOnNext(resp -> log.debug(resp.toString()))
                .doOnError(e -> log.error(e.getMessage(), e))
                .flatMap(resp -> resp.bodyToMono(String.class))
                .map(r -> event)
                .onErrorResume(e -> Mono.just(BackendErrorEvent.of(event, e)));
    }

    private WebClient.RequestBodySpec prepareRequest(Backend<HttpSettings> backend) {
        return createWebClient(backend.settings())
                .post()
                .uri(backend.destination())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> backend.settings().getAdditionalHeaders().forEach(httpHeaders::add));
    }

    //@ToDo -> maybe caching this webclient
    private WebClient createWebClient(HttpSettings httpSettings) {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient -> {
                    // @ToDo add connect timeout to endpoint configuration
                    tcpClient = tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);
                    tcpClient = tcpClient.doOnConnected(conn -> conn
                            .addHandlerLast(new ReadTimeoutHandler(httpSettings.getTimeoutInMillis(), MILLISECONDS)));
                    return tcpClient;
                });
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder().clientConnector(connector).build();
    }
}
