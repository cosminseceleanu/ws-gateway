package com.cosmin.wsgateway.tests;

import com.cosmin.wsgateway.api.Application;
import com.cosmin.wsgateway.tests.client.ConnectionsClient;
import com.cosmin.wsgateway.tests.client.EndpointsClient;
import com.cosmin.wsgateway.tests.client.WebSocketClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@ActiveProfiles("tests")
public abstract class BaseTestIT {
    private static final Logger logger = LoggerFactory.getLogger("FunctionalTests");

    @LocalServerPort
    protected int port;

    @Autowired
    protected WebTestClient webClient;

    protected EndpointsClient endpointsClient;
    protected ConnectionsClient connectionsClient;
    protected WebSocketClient webSocketClient;

    private final AtomicBoolean isConfigured = new AtomicBoolean();

    @BeforeEach()
    void configure() {
        if (isConfigured.get()) {
            return;
        }
        endpointsClient = new EndpointsClient(webClient);
        connectionsClient = new ConnectionsClient(webClient);
        webSocketClient = new WebSocketClient(8081);
        isConfigured.compareAndSet(false, true);
    }

    public void await() {
        await(Duration.ofSeconds(10));
    }

    public void await(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Assertions.fail(e);
        }
    }

    protected static void Given(String msg) {
        logger.info("Given: {}", msg);
    }

    protected static void When(String msg) {
        logger.info("When: {}", msg);
    }

    protected static void Then(String msg) {
        logger.info("Then: {}", msg);
    }
}
