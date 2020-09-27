package com.cosmin.wsgateway.tests.gateway;

import com.cosmin.wsgateway.tests.BaseTestIT;
import com.cosmin.wsgateway.tests.common.EndpointFixtures;
import java.time.Duration;
import java.util.UUID;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

public class HeartbeatIT extends BaseTestIT {

    //ToDo enable this when you find a way to disable sending pong messages
    public void idleConnectionIsClosedAfter() {
        Given("An endpoint");
        var connectionId = UUID.randomUUID().toString();
        var endpoint = EndpointFixtures.getRepresentation("/heartbeat");
        endpoint.getGeneralSettings().setHeartbeatIntervalInSeconds(5);
        endpoint.getGeneralSettings().setHeartbeatMaxMissingPingFrames(2);
        endpointsClient.createAndAssert(endpoint);

        When("A WS connection is created and doesn't respond to ping messages");
        var connection = webSocketClient.connect("/heartbeat", connectionId);

        Then("Connection should be closed");
        Awaitility.await("Connection should be closed after 2 ping messages")
                .atMost(Duration.ofSeconds(15))
                .until(connection::isClosed);
    }

    @Test
    public void connectionIsKeptAliveWhenIsActive() {
        Given("An endpoint");
        var connectionId = UUID.randomUUID().toString();
        var endpoint = EndpointFixtures.getRepresentation("/heartbeat");
        endpoint.getGeneralSettings().setHeartbeatIntervalInSeconds(5);
        endpoint.getGeneralSettings().setHeartbeatMaxMissingPingFrames(2);
        endpointsClient.createAndAssert(endpoint);

        When("A WS connection is created and respond to ping messages");
        var connection = webSocketClient.connect("/heartbeat", connectionId);

        Then("Connection is alive and accept messages");
        Awaitility.await("Connection is alive")
                .during(Duration.ofSeconds(15))
                .atMost(Duration.ofSeconds(25))
                .until(() -> !connection.isClosed());

        connection.send("{}");
    }
}
