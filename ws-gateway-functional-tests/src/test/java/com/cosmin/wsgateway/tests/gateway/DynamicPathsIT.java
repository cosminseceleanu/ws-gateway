package com.cosmin.wsgateway.tests.gateway;

import com.cosmin.wsgateway.tests.BaseTestIT;
import com.cosmin.wsgateway.tests.Tags;
import com.cosmin.wsgateway.tests.client.WebSocketConnectionException;
import com.cosmin.wsgateway.tests.common.EndpointFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tags.Gateway
public class DynamicPathsIT extends BaseTestIT {

    @Test
    public void failToConnectWhenEndpointIsMissing() {
        var thrown = assertThrows(WebSocketConnectionException.class, () -> {
            webSocketClient.connect("/not-found");
        });
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());
    }

    @Test
    public void connectionIsAcceptedIfEndpointMatchesPath() {
        var endpoint = endpointsClient.createAndAssert(EndpointFixtures.getRepresentation("/aa/ddd"));
        var connection = webSocketClient.connect(endpoint.getPath());
        connection.send("{\"foo\": \"bar\"}");
        connection.send("{\"foo\": \"bar\"}");
    }

    @Test
    public void connectionIsAcceptedIfEndpointRegexMatchesPath() {
        endpointsClient.createAndAssert(EndpointFixtures.getRepresentation("/aa/(.*)"));
        var connection = webSocketClient.connect("/aa/bb/ccc/ddd");
        connection.send("{\"foo\": \"bar\"}");
    }
}
