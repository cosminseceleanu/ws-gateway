package com.cosmin.wsgateway.tests.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cosmin.wsgateway.tests.BaseTestIT;
import com.cosmin.wsgateway.tests.common.EndpointFixtures;
import com.cosmin.wsgateway.tests.common.JsonUtils;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class OutboundEventsFlowIT extends BaseTestIT {
    @Test
    public void gatewayClientReceivesEventsSentViaConnectionsApi() {
        Given("an websocket connection");
        var connectionId = UUID.randomUUID().toString();
        var event1 = JsonUtils.toJson(Map.of("foo", "bar"));
        var event2 = JsonUtils.toJson(Map.of("foo", "bar2"));

        var endpoint = EndpointFixtures.getRepresentation("/outbound-events/test-1");
        endpointsClient.createAndAssert(endpoint);
        var connection = webSocketClient.connect(endpoint.getPath(), connectionId);


        When("Connections outbound api is called");
        connectionsClient.sendOutbound(connectionId, event1);
        connectionsClient.sendOutbound(connectionId, event2);

        Then("Gateway client receives msgs via api");
        await(Duration.ofMillis(3000));

        assertEquals(2, connection.getReceivedMessages().size());
        assertTrue(connection.getReceivedMessages().contains(event1));
        assertTrue(connection.getReceivedMessages().contains(event2));
    }
}
