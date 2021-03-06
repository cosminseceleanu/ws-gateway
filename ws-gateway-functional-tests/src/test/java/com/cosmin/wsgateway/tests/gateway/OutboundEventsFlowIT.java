package com.cosmin.wsgateway.tests.gateway;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cosmin.wsgateway.tests.BaseTestIT;
import com.cosmin.wsgateway.tests.Tags;
import com.cosmin.wsgateway.tests.common.EndpointFixtures;
import com.cosmin.wsgateway.tests.common.JsonUtils;
import com.cosmin.wsgateway.tests.utils.Conditions;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

@Tags.Gateway
@Tags.PubSub
public class OutboundEventsFlowIT extends BaseTestIT {
    @Test
    public void gatewayClientReceivesEventsSentViaConnectionsApi() {
        Given("an websocket connection");
        var connectionId = UUID.randomUUID().toString();
        var event1 = JsonUtils.toJson(Map.of("foo", "bar"));
        var event2 = JsonUtils.toJson(List.of("foo", "bar2"));

        var endpoint = EndpointFixtures.getRepresentation("/outbound-events/test-1");
        endpointsClient.createAndAssert(endpoint);
        var connection = webSocketClient.connect(endpoint.getPath(), connectionId);
        Awaitility.await().during(Duration.ofMillis(200)).until(() -> true);

        When("Connections outbound api is called");
        connectionsClient.sendEvent(connectionId, event1);
        connectionsClient.sendEvent(connectionId, event2);

        Then("Gateway client receives msgs via api");
        Awaitility.await("should receive 2 messages")
                .atMost(Duration.ofSeconds(20))
                .until(Conditions.receivedMessages(connection), is(2));

        assertTrue(connection.getReceivedMessages().contains(event1));
        assertTrue(connection.getReceivedMessages().contains(event2));
    }
}
