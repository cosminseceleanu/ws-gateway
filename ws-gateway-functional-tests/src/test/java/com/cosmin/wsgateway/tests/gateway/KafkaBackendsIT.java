package com.cosmin.wsgateway.tests.gateway;

import static com.cosmin.wsgateway.tests.common.EndpointFixtures.createRouteWithKafkaBackend;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cosmin.wsgateway.api.representation.RouteRepresentation;
import com.cosmin.wsgateway.tests.BaseTestIT;
import com.cosmin.wsgateway.tests.Tags;
import com.cosmin.wsgateway.tests.common.EndpointFixtures;
import com.cosmin.wsgateway.tests.common.JsonUtils;
import com.cosmin.wsgateway.tests.utils.KafkaUtils;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

@Tags.Gateway
public class KafkaBackendsIT extends BaseTestIT {

    @Test
    public void kafkaBackendReceivesConnectAndDisconnectEvent() {
        Given("An endpoint with kafka backends");
        var connectTopic = "test.1.connect";
        var disconnectTopic = "test.1.disconnect";
        var connectionId = UUID.randomUUID().toString();
        var expectedPayload = JsonUtils.toJson(Map.of("connectionId", connectionId));
        var endpoint = EndpointFixtures.getRepresentation("/kafka-backend/test-1", getRoutes(
                connectTopic, disconnectTopic, "default"
        ));
        endpointsClient.createAndAssert(endpoint);
        embeddedKafkaBroker.addTopics(connectTopic, disconnectTopic);

        When("A WS connection is created with the gateway");
        var connection = webSocketClient.connect("/kafka-backend/test-1", connectionId);
        connection.disconnect();
        Awaitility.await("disconnect event should be sent")
            .atMost(Duration.ofSeconds(6))
            .until(() -> KafkaUtils.getRecords(disconnectTopic, embeddedKafkaBroker).size(), is(1));

        Then("Kafka backend for connect and disconnect routes has received connection events");
        var connectionEvents = KafkaUtils.getRecords(connectTopic, embeddedKafkaBroker);
        var disconnectEvents = KafkaUtils.getRecords(disconnectTopic, embeddedKafkaBroker);

        assertEquals(1, connectionEvents.size());
        assertEquals(expectedPayload, connectionEvents.get(0));

        assertEquals(expectedPayload, disconnectEvents.get(0));
    }

    @Test
    public void kafkaBackendReceivesUserEvents() {
        Given("An endpoint with kafka backends");
        var connectionId = UUID.randomUUID().toString();
        var expectedPayload = JsonUtils.toJson(Map.of("foo", "bar"));
        var endpoint = EndpointFixtures.getRepresentation("/kafka-backend/test-2", getRoutes(
                "test.2.connect",
                "test.2.disconnect",
                "test.2.default"
        ));
        endpointsClient.createAndAssert(endpoint);
        embeddedKafkaBroker.addTopics("test.2.default");

        When("A WS connection is created and events are sent");
        var connection = webSocketClient.connect("/kafka-backend/test-2", connectionId);
        connection.send(expectedPayload);
        connection.send(expectedPayload);
        connection.send(expectedPayload);
        connection.send(expectedPayload);
        Awaitility.await("events are received")
                .atMost(Duration.ofSeconds(6))
                .until(() -> KafkaUtils.getRecords("test.2.default", embeddedKafkaBroker).size(), is(4));

        Then("Kafka backend receives user sent events");
        var events = KafkaUtils.getRecords("test.2.default", embeddedKafkaBroker);

        assertEquals(expectedPayload, events.get(0));
    }

    private Set<RouteRepresentation> getRoutes(String connectTopic, String disconnectTopic, String defaultTopic) {
        String brokers = embeddedKafkaBroker.getBrokersAsString();
        Given("kafka brokers=" + brokers);

        return Set.of(
                createRouteWithKafkaBackend(RouteRepresentation.Type.CONNECT, connectTopic, brokers),
                createRouteWithKafkaBackend(RouteRepresentation.Type.DEFAULT, defaultTopic, brokers),
                createRouteWithKafkaBackend(RouteRepresentation.Type.DISCONNECT, disconnectTopic, brokers)
        );
    }
}
