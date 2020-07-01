package com.cosmin.wsgateway.infrastructure.gateway.connectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

import com.cosmin.wsgateway.application.gateway.connection.events.BackendErrorEvent;
import com.cosmin.wsgateway.application.gateway.connector.BackendConnector;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.Event;
import com.cosmin.wsgateway.domain.backends.HttpSettings;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final ConcurrentHashMap<HttpSettings, WebClient> webClients = new ConcurrentHashMap<>();

    @Override
    public boolean supports(Backend.Type type) {
        return Backend.Type.HTTP.equals(type);
    }

    @Override
    public Mono<Event> sendEvent(Event event, Backend<HttpSettings> backend) {
        return prepareRequest(backend)
                .body(Mono.just(event.payload()), Object.class)
                .exchange()
                .flatMap(resp -> resp.toEntity(String.class))
                .map(r -> handleResponse(event, backend, r))
                .doOnError(e -> log.error(e.getMessage(), e))
                .onErrorResume(e -> Mono.just(BackendErrorEvent.of(event, e)));
    }

    private WebClient.RequestBodySpec prepareRequest(Backend<HttpSettings> backend) {
        return createWebClient(backend.settings())
                .post()
                .uri(backend.destination())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> backend.settings().getAdditionalHeaders().forEach(httpHeaders::add));
    }

    private WebClient createWebClient(HttpSettings httpSettings) {
        if (webClients.containsKey(httpSettings)) {
            return webClients.get(httpSettings);
        }
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient -> {
                    tcpClient = tcpClient.option(
                            ChannelOption.CONNECT_TIMEOUT_MILLIS,
                            httpSettings.getConnectTimeoutInMillis()
                    );
                    tcpClient = tcpClient.doOnConnected(conn -> conn
                            .addHandlerLast(new ReadTimeoutHandler(
                                    httpSettings.getReadTimeoutInMillis(),
                                    MILLISECONDS
                            )));
                    return tcpClient;
                });
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        var client = WebClient.builder().clientConnector(connector).build();
        webClients.put(httpSettings, client);

        return client;
    }

    private Event handleResponse(Event event, Backend<HttpSettings> backend, ResponseEntity<String> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return event;
        }
        log.warn("Invalid status code received from {} {}",
                keyValue("backend", backend.destination()),
                keyValue("status", responseEntity.getStatusCodeValue())
        );
        return BackendErrorEvent.of(event, new InvalidStatusCodeException(String.format(
                "Invalid status code from backend=%s expected=20x got=%s",
                backend.destination(),
                responseEntity.getStatusCodeValue()
        )));
    }
}
