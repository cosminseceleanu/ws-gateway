package com.cosmin.wsgateway.api.vertx;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.cosmin.wsgateway.application.gateway.GatewayMetrics;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import com.cosmin.wsgateway.application.gateway.connection.ConnectionManager;
import com.cosmin.wsgateway.application.gateway.connection.ConnectionRequest;
import com.cosmin.wsgateway.application.gateway.connection.Message;
import com.cosmin.wsgateway.application.gateway.exceptions.AccessDeniedException;
import com.cosmin.wsgateway.application.gateway.exceptions.AuthenticationException;
import com.cosmin.wsgateway.domain.exceptions.EndpointNotFoundException;
import com.cosmin.wsgateway.infrastructure.GatewayProperties;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
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

    private final ConnectionManager connectionManager;
    private final GatewayMetrics gatewayMetrics;
    private final GatewayProperties gatewayProperties;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        HttpServer server = vertx.createHttpServer();
        server.webSocketHandler(this::handleWebsocketConnection);

        server.listen(gatewayProperties.getVertx().getGatewayPort(), res -> {
            if (res.succeeded()) {
                log.info("Web Socket verticle has successfully started !!!");
            } else {
                log.error("Web Socket verticle did not started!");
            }
        });
    }

    private void handleWebsocketConnection(ServerWebSocket serverWebSocket) {
        ConnectionRequest request = createConnectionRequest(serverWebSocket);
        Promise<Integer> handshakePromise = Promise.promise();
        serverWebSocket.setHandshake(handshakePromise.future());

        var processor = new WebSocketMessageProcessor();
        Flux<Message> inboundMessages = createInboundFlux(processor);
        doConnect(serverWebSocket, request, handshakePromise, processor, inboundMessages);
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

    private Flux<Message> createInboundFlux(WebSocketMessageProcessor processor) {
        return Flux.create(sink -> processor.setListener(new WebSocketMessageListener() {
            @Override
            public void onMessage(Message message) {
                sink.next(message);
            }

            @Override
            public void onClose() {
                sink.complete();
            }
        }));
    }

    private void doConnect(ServerWebSocket serverWebSocket, ConnectionRequest request,
                           Promise<Integer> handshakePromise, WebSocketMessageProcessor processor,
                           Flux<Message> inboundMessages
    ) {
        connectionManager.connect(request)
                .doOnSuccess(c -> acceptConnection(serverWebSocket, handshakePromise, processor, c))
                .doOnError(e -> handleConnectionError(e, handshakePromise))
                .onErrorStop()
                .flatMapMany(bridge -> bridge.handle(inboundMessages))
                .subscribe(msg ->
                    writeMessage(serverWebSocket, msg), e ->
                    serverWebSocket.close((short) INTERNAL_SERVER_ERROR.value(), "Unknown error"));
    }

    private void acceptConnection(ServerWebSocket serverWebSocket, Promise<Integer> handshakePromise,
                                  WebSocketMessageProcessor processor, Connection connection) {
        gatewayMetrics.recordConnection(connection.getEndpoint());
        serverWebSocket.textMessageHandler(msg -> {
            gatewayMetrics.recordInboundEventReceived(connection.getEndpoint(), connection.getId());
            processor.onMessage(Message.text(msg));
        });
        serverWebSocket.closeHandler(h -> {
            gatewayMetrics.recordDisconnect(connection.getEndpoint());
            processor.onClose();
        });
        serverWebSocket.pongHandler(b -> {
            log.trace("Received pong message {}", keyValue("connectionId", connection.getId()));
            processor.onMessage(Message.pong());
        });
        log.info("Accept gateway WS {}", keyValue("connectionId", connection.getId()));
        handshakePromise.complete(101);
    }

    private void handleConnectionError(Throwable error, Promise<Integer> handshakePromise) {
        int status = INTERNAL_SERVER_ERROR.value();
        if (error instanceof EndpointNotFoundException) {
            status = NOT_FOUND.value();
        }
        if (error instanceof AuthenticationException) {
            status = UNAUTHORIZED.value();
        }
        if (error instanceof AccessDeniedException) {
            status = FORBIDDEN.value();
        }
        log.error("Reject gateway WS connection with {}", keyValue("status", status), error);
        gatewayMetrics.recordConnectionError(status);
        handshakePromise.complete(status);
    }

    private void writeMessage(ServerWebSocket serverWebSocket, Message msg) {
        if (log.isTraceEnabled()) {
            log.trace("Send {} to user", keyValue("msg", msg));
        }
        if (msg.isPing()) {
            serverWebSocket.writePing(Buffer.buffer(msg.getPayload()));
        } else if (msg.isPoisonPill()) {
            serverWebSocket.close((short) 409, msg.getPayload());
        } else {
            serverWebSocket.writeTextMessage(msg.getPayload());
        }
    }

    @Setter
    private static class WebSocketMessageProcessor {
        private WebSocketMessageListener listener;

        void onMessage(Message message) {
            listener.onMessage(message);
        }

        void onClose() {
            listener.onClose();
        }
    }

    private interface WebSocketMessageListener {
        void onMessage(Message message);

        void onClose();
    }
}
