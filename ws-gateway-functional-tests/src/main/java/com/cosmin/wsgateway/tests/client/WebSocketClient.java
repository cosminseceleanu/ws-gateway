package com.cosmin.wsgateway.tests.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocketHandshakeException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;


@Slf4j
public class WebSocketClient {
    private String host = "ws://localhost";
    private int port;

    public WebSocketClient(int port) {
        this.port = port;
    }

    public WebSocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public WebSocketConnection connect(String path) {
        String connectionId = UUID.randomUUID().toString();
        return connect(path, connectionId);
    }

    public WebSocketConnection connect(String path, Map<String, String> headers) {
        String connectionId = UUID.randomUUID().toString();
        return connect(path, connectionId, headers);
    }

    public WebSocketConnection connect(String path, String connectionId) {
        return connect(path, connectionId, Collections.emptyMap());
    }

    public WebSocketConnection connect(String path, String connectionId, Map<String, String> headers) {
        var uri = URI.create(String.format("%s:%s%s?cid=%s", host, port, path, connectionId));
        Queue<String> receivedMessages = new ConcurrentLinkedQueue<>();

        HttpClient client = HttpClient.newHttpClient();
        try {
            var builder = client.newWebSocketBuilder();
            for (Map.Entry<String, String> header: headers.entrySet()) {
                builder = builder.header(header.getKey(), header.getValue());
            }
            WebSocket webSocket = builder.buildAsync(uri, new WebSocketListener(receivedMessages))
                    .get(15, TimeUnit.SECONDS);

            return new WebSocketConnection(webSocket, receivedMessages);
        } catch (InterruptedException | TimeoutException e) {
            log.error(e.getMessage(), e);
            Assertions.fail("Unable to establish WS connection");
            return null;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof WebSocketHandshakeException) {
                throw new WebSocketConnectionException(
                        HttpStatus.valueOf(((WebSocketHandshakeException) e.getCause()).getResponse().statusCode()),
                        e.getCause()
                );
            }
            Assertions.fail("Unable to establish WS connection");
            return null;
        }
    }

    @Setter
    public static class WebSocketConnection {
        private WebSocket socket;
        private Queue<String> receivedMessages;
        private String connectionId;

        public WebSocketConnection(WebSocket socket, Queue<String> receivedMessages) {
            this.socket = socket;
            this.receivedMessages = receivedMessages;
        }

        public List<String> getReceivedMessages() {
            return new ArrayList<>(receivedMessages);
        }

        public String getConnectionId() {
            return connectionId;
        }

        public void send(String msg) {
            try {
                sendAsync(msg).get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error(e.getMessage(), e);
                Assertions.fail("Failed to send msg");
            }
        }

        public CompletableFuture<?> sendAsync(String msg) {
            return socket.sendText(msg, true);
        }

        public void disconnect() {
            try {
                disconnectAsync().get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error(e.getMessage(), e);
                Assertions.fail("Unable to close WS connection");
            }
        }

        public CompletableFuture<?> disconnectAsync() {
            return socket.sendClose(WebSocket.NORMAL_CLOSURE, "");
        }

        public boolean isClosed() {
            return socket.isInputClosed();
        }
    }

    @AllArgsConstructor
    public static class WebSocketListener implements WebSocket.Listener {
        private Queue<String> receivedMessages;

        @Override
        public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
            return null;
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            webSocket.request(1);
            return CompletableFuture.completedFuture(data)
                    .thenAccept(msg -> receivedMessages.offer(msg.toString()));
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            log.info("socket was closed status={} reason={}", statusCode, reason);
            return CompletableFuture.completedFuture(statusCode);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            log.error("socker error={}", error.getMessage(), error);
        }
    }
}
