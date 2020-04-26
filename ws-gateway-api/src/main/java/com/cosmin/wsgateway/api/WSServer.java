package com.cosmin.wsgateway.api;

import com.cosmin.wsgateway.application.gateway.connection.ConnectionManager;
import com.cosmin.wsgateway.application.gateway.connection.ConnectionRequest;
import com.cosmin.wsgateway.domain.exceptions.EndpointNotFoundException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;

import io.vertx.core.http.ServerWebSocket;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@Slf4j
@RequiredArgsConstructor
public class WSServer extends AbstractVerticle {

    public static final int DEFAULT_PORT = 8081;

    private final ConnectionManager connectionManager;

    private final Scheduler scheduler = Schedulers.newBoundedElastic(
            Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE,
            Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE,
            "gateway-pool"
    );

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        HttpServer server = vertx.createHttpServer();
        server.webSocketHandler(this::handleWebsocketConnection);

        server.listen(DEFAULT_PORT, res -> {
            if (res.succeeded()) {
                log.info("Web Socket Server has successfully started!");
            } else {
                log.info("Web Socket Server did not started!");
            }
        });
    }

    private void handleWebsocketConnection(ServerWebSocket serverWebSocket) {
        var request = new ConnectionRequest(
                null,
                null,
                serverWebSocket.path()
        );
        var processor = new WSMessageProcessor();
        Flux<String> wsMessages = createInboundFlux(processor).publishOn(scheduler);

        connectionManager.connect(request)
                .doOnSuccess(c -> {
                    serverWebSocket.textMessageHandler(processor::onMessage);
                    serverWebSocket.closeHandler(h -> processor.onClose());
                    log.debug("Accept WS connection={}", c.getId());
                    serverWebSocket.accept();
                })
                //connection need to be closed on the same vert.x thread
                .doOnError(e -> handleConnectionError(serverWebSocket, e))
                .onErrorStop()
                //then we start the event processing on the gateway scheduler
                .publishOn(scheduler)
                .flatMapMany(bridge -> bridge.handle(wsMessages))
                .subscribe(msg -> {
                    log.debug("Send msg={} to user", msg);
                    serverWebSocket.writeTextMessage(msg);
                });
    }

    private Flux<String> createInboundFlux(WSMessageProcessor processor) {
        return Flux.create(sink -> processor.setListener(new WSMessageListener() {
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
        log.error("Reject WS connection with status={}", status, error);
        serverWebSocket.reject(status);
    }

    @Setter
    private static class WSMessageProcessor {
        private WSMessageListener listener;

        void onMessage(String message) {
            listener.onMessage(message);
        }

        void onClose() {
            listener.onClose();
        }
    }

    private interface WSMessageListener {
        void onMessage(String message);
        void onClose();
    }
}
