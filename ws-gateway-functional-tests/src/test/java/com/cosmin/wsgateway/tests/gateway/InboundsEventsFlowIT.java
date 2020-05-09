package com.cosmin.wsgateway.tests.gateway;

import com.cosmin.wsgateway.api.representation.RouteRepresentation;
import com.cosmin.wsgateway.tests.BaseTestIT;
import com.cosmin.wsgateway.tests.common.EndpointFixtures;
import com.cosmin.wsgateway.tests.common.JsonUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.cosmin.wsgateway.tests.common.EndpointFixtures.createRouteWithHttpBackend;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class InboundsEventsFlowIT extends BaseTestIT {
    public static WireMockServer wiremock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    private static String backendUrl;

    private Set<RouteRepresentation> httpRoutes = Set.of(
            createRouteWithHttpBackend(RouteRepresentation.Type.CONNECT, backendUrl + "/connect"),
            createRouteWithHttpBackend(RouteRepresentation.Type.DEFAULT, backendUrl + "/default"),
            createRouteWithHttpBackend(RouteRepresentation.Type.DISCONNECT, backendUrl + "/disconnect")
    );

    @BeforeAll
    static void beforeAll() {
        wiremock.start();
        backendUrl = String.format("http://localhost:%d", wiremock.port());
    }

    @AfterAll
    static void afterAll() {
        wiremock.stop();
    }

    @BeforeEach
    void setUp() {
        wiremock.resetAll();
    }

    @Test
    public void httpBackendIsCalledWithConnectAndDisconnectEvent() {
        Given("An endpoint with http backends and a backend server");
        var connectionId = UUID.randomUUID().toString();
        var expectedPayload = JsonUtils.toJson(Map.of("connectionId", connectionId));
        var endpoint = EndpointFixtures.getRepresentation("/http-backend/test-1", httpRoutes);
        endpointsClient.createAndAssert(endpoint);

        wiremock.stubFor(post(urlPathEqualTo("/connect"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedPayload)));
        wiremock.stubFor(post(urlPathEqualTo("/disconnect"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedPayload)));

        When("A WS connection is created with the gateway");
        var connection = webSocketClient.connect("/http-backend/test-1", connectionId);
        await(Duration.ofMillis(1000));
        connection.disconnect();
        await(Duration.ofMillis(1000));

        Then("Http backend for connect and disconnect routes is called with connection events");
        wiremock.verify(1, postRequestedFor(urlPathEqualTo("/connect"))
                .withHeader("Content-Type", equalToIgnoreCase("application/json"))
                .withRequestBody(equalTo(expectedPayload)));
    }

    @Test
    public void httpBackendIsCalledWithEventsSentThroughWSConnection() {
        Given("An endpoint with http backends and a backend server");
        var connectionId = UUID.randomUUID().toString();
        var expectedPayload = JsonUtils.toJson(Map.of("foo", "bar"));
        var endpoint = EndpointFixtures.getRepresentation("/http-backend/test-2", httpRoutes);
        endpointsClient.createAndAssert(endpoint);

        wiremock.stubFor(post(urlPathEqualTo("/default"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedPayload)));

        When("A WS connection is created and events are sent");
        var connection = webSocketClient.connect("/http-backend/test-2", connectionId);
        connection.send(expectedPayload);
        connection.send(expectedPayload);


        await(Duration.ofMillis(1500));

        Then("Http backend is called with user events");
        wiremock.verify(2, postRequestedFor(urlPathEqualTo("/default"))
                .withHeader("Content-Type", equalToIgnoreCase("application/json"))
                .withRequestBody(equalTo(expectedPayload)));
    }
}
