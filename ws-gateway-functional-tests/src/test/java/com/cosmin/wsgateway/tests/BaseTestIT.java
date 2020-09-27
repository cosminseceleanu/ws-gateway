package com.cosmin.wsgateway.tests;

import com.cosmin.wsgateway.api.Application;
import com.cosmin.wsgateway.tests.client.ConnectionsClient;
import com.cosmin.wsgateway.tests.client.EndpointsClient;
import com.cosmin.wsgateway.tests.client.WebSocketClient;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@EmbeddedKafka(partitions = 1)
public abstract class BaseTestIT {
    private static final Logger logger = LoggerFactory.getLogger("FunctionalTests");

    @LocalServerPort
    protected int port;

    @Autowired
    protected WebTestClient webClient;

    @Autowired
    protected EmbeddedKafkaBroker embeddedKafkaBroker;

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
