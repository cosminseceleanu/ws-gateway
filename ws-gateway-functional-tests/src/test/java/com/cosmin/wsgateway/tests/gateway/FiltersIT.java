package com.cosmin.wsgateway.tests.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cosmin.wsgateway.tests.BaseTestIT;
import com.cosmin.wsgateway.tests.client.WebSocketConnectionException;
import com.cosmin.wsgateway.tests.common.EndpointFixtures;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class FiltersIT extends BaseTestIT {

    public static final String HEADER_X_FORWARDED_FOR = "X_FORWARDED_FOR";

    @Test
    public void connectionFromABlacklistedHostIsRejected() {
        Given("an endpoint with blacklist host filter");
        var endpoint = EndpointFixtures.getRepresentation("/black-list");
        endpoint.getFilters().setBlacklistHosts(Set.of("localhost:8081"));
        endpointsClient.createAndAssert(endpoint);

        When("connection from black listed host is attempted");
        var thrown = assertThrows(WebSocketConnectionException.class, () -> {
            webSocketClient.connect(endpoint.getPath());
        });

        Then("connection is rejected with 403");
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    public void connectionFromAHostThatIsNotWhitelistedIsRejected() {
        Given("an endpoint with whitelist host filter");
        var endpoint = EndpointFixtures.getRepresentation("/host-whitelist");
        endpoint.getFilters().setWhitelistHosts(Set.of("host whitelisted"));
        endpointsClient.createAndAssert(endpoint);

        When("connection from a host that is not whitelisted is attempted");
        var thrown = assertThrows(WebSocketConnectionException.class, () -> {
            webSocketClient.connect(endpoint.getPath());
        });

        Then("connection is rejected with 403");
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    public void onlyConnectionFromWhitelistedHostIsAccepted() {
        Given("an endpoint with whitelist host filter");
        var endpoint = EndpointFixtures.getRepresentation("/host-whitelist-2");
        endpoint.getFilters().setWhitelistHosts(Set.of("localhost:8081", "host2"));
        endpointsClient.createAndAssert(endpoint);

        When("connection from a whitelisted host is attempted");
        webSocketClient.connect(endpoint.getPath());
        Then("connection is accepted");
    }

    @Test
    public void connectionFromABlacklistedIpIsRejected() {
        Given("an endpoint with blacklist ip filter");
        var endpoint = EndpointFixtures.getRepresentation("/black-list");
        endpoint.getFilters().setBlacklistIps(Set.of("127.0.0.1"));
        endpointsClient.createAndAssert(endpoint);

        When("connection from black listed ip is attempted");
        var thrown = assertThrows(WebSocketConnectionException.class, () -> {
            webSocketClient.connect(endpoint.getPath(), Map.of(HEADER_X_FORWARDED_FOR, "127.0.0.1"));
        });

        Then("connection is rejected with 403");
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    public void connectionFromAnIpThatIsNotWhitelistedIsRejected() {
        Given("an endpoint with whitelist ip filter");
        var endpoint = EndpointFixtures.getRepresentation("/host-whitelist");
        endpoint.getFilters().setWhitelistIps(Set.of("145.4.5.7", "164.0.0.233"));
        endpointsClient.createAndAssert(endpoint);

        When("connection from an ip that is not whitelisted is attempted");
        var thrown = assertThrows(WebSocketConnectionException.class, () -> {
            webSocketClient.connect(endpoint.getPath(), Map.of(HEADER_X_FORWARDED_FOR, "127.0.0.1"));
        });

        Then("connection is rejected with 403");
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @Test
    public void onlyConnectionFromIpHostIsAccepted() {
        Given("an endpoint with whitelist ip filter");
        var endpoint = EndpointFixtures.getRepresentation("/host-whitelist-2");
        endpoint.getFilters().setWhitelistIps(Set.of("145.4.5.7"));
        endpointsClient.createAndAssert(endpoint);

        When("connection from a whitelisted ip is attempted");
        webSocketClient.connect(endpoint.getPath(), Map.of(HEADER_X_FORWARDED_FOR, "145.4.5.7"));
        Then("connection is accepted");
    }
}
