package com.cosmin.wsgateway.api.vertx;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.cosmin.wsgateway.application.gateway.connection.ConnectionManager;
import com.cosmin.wsgateway.application.gateway.connection.ConnectionRequest;
import com.cosmin.wsgateway.application.gateway.exceptions.AuthenticationException;
import com.cosmin.wsgateway.domain.exceptions.EndpointNotFoundException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;


@Slf4j
@RequiredArgsConstructor
public class WebSocketServer extends AbstractVerticle {

    public static final int DEFAULT_PORT = 8081;

    private final ConnectionManager connectionManager;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        HttpServer server = vertx.createHttpServer();
        server.webSocketHandler(this::handleWebsocketConnection);

        server.listen(DEFAULT_PORT, res -> {
            if (res.succeeded()) {
                log.info("Web Socket verticle has successfully started!");
            } else {
                log.error("Web Socket verticle did not started!");
            }
        });
    }

    private void handleWebsocketConnection(ServerWebSocket serverWebSocket) {
        ConnectionRequest request = createConnectionRequest(serverWebSocket);
        var processor = new WebSocketSMessageProcessor();
        Flux<String> inboundMessages = createInboundFlux(processor);

        connectionManager.connect(request)
                .doOnSuccess(c -> {
                    serverWebSocket.textMessageHandler(processor::onMessage);
                    serverWebSocket.closeHandler(h -> processor.onClose());
                    log.debug("Accept WS connection={}", c.getId());
                    serverWebSocket.accept();
                })
                .doOnError(e -> handleConnectionError(serverWebSocket, e))
                .onErrorStop()
                .flatMapMany(bridge -> bridge.handle(inboundMessages))
                .subscribe(msg -> {
                    log.trace("Send msg={} to user", msg);
                    serverWebSocket.writeTextMessage(msg);
                });
    }

    private ConnectionRequest createConnectionRequest(ServerWebSocket serverWebSocket) {
        var headers = new LinkedHashMap<String, String>();
        serverWebSocket.headers()
                .forEach(entry -> headers.put(entry.getKey(), entry.getValue()));
        serverWebSocket.query();
        return new ConnectionRequest(
                headers,
                getQueryMap(serverWebSocket.query()),
                serverWebSocket.path()
        );
    }

    private Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    private Flux<String> createInboundFlux(WebSocketSMessageProcessor processor) {
        return Flux.create(sink -> processor.setListener(new WebSocketMessageListener() {
                @Override
                public void onMessage(String message) {
                    sink.next(message);
                }

                @Override
                public void onClose() {
                    sink.complete();
                }
            }));
    }

    private void handleConnectionError(ServerWebSocket serverWebSocket, Throwable error) {
        int status = INTERNAL_SERVER_ERROR.value();
        if (error instanceof EndpointNotFoundException) {
            status = NOT_FOUND.value();
        }
        if (error instanceof AuthenticationException) {
            status = UNAUTHORIZED.value();
        }
        log.error("Reject WS connection with status={}", status, error);
        serverWebSocket.reject(status);
    }

    @Setter
    private static class WebSocketSMessageProcessor {
        private WebSocketMessageListener listener;

        void onMessage(String message) {
            listener.onMessage(message);
        }

        void onClose() {
            listener.onClose();
        }
    }

    private interface WebSocketMessageListener {
        void onMessage(String message);

        void onClose();
    }
}
