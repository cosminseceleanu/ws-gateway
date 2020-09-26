package com.cosmin.wsgateway.tests.gateway;

import static com.cosmin.wsgateway.tests.common.EndpointFixtures.createRouteWithHttpBackend;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.Matchers.greaterThan;

import com.cosmin.wsgateway.api.representation.EndpointRepresentation;
import com.cosmin.wsgateway.api.representation.RouteRepresentation;
import com.cosmin.wsgateway.tests.BaseTestIT;
import com.cosmin.wsgateway.tests.common.EndpointFixtures;
import com.cosmin.wsgateway.tests.common.JsonUtils;
import com.cosmin.wsgateway.tests.utils.Conditions;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PayloadsIT extends BaseTestIT {

    private static final WireMockServer wiremock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    private static String backendUrl;
    private EndpointRepresentation endpoint;

    private final Set<RouteRepresentation> httpRoutes = Set.of(
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

    private static Stream<Arguments> gatewayPayloads() {
        return Stream.of(
                Arguments.of(JsonUtils.toJson(Map.of("key", "value")), "Gateway should accept json objects"),
                Arguments.of(JsonUtils.toJson(List.of("one", "two")), "Gateway should accept json array"),
                Arguments.of(JsonUtils.toJson(List.of(Map.of("foo", "bar"), Map.of("bar", "foo"))), "Gateway should accept json array of objects"),
                Arguments.of(JsonUtils.toJson("hello world"), "Gateway should accept string json"),
                Arguments.of(JsonUtils.toJson(1.12), "Gateway should accept numeric json")
        );
    }

    @ParameterizedTest
    @MethodSource("gatewayPayloads")
    public void gatewayConnectionShouldAcceptAllJsonValues(String json, String description) {
        Given("An endpoint with http backends and a backend server");
        var connectionId = UUID.randomUUID().toString();
        if (endpoint == null) {
            endpoint = EndpointFixtures.getRepresentation("/payloads/test", httpRoutes);
            endpointsClient.createAndAssert(endpoint);
        }

        wiremock.stubFor(post(urlPathEqualTo("/default"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)));

        When("A WS connection is created and events are sent");
        var connection = webSocketClient.connect("/payloads/test", connectionId);
        connection.send(json);
        connection.disconnect();

        Awaitility.await("backends should receive messages")
                .atMost(Duration.ofSeconds(15))
                .until(Conditions.receivedRequest(wiremock), greaterThan(1));

        Then(description);
        wiremock.verify(1, postRequestedFor(urlPathEqualTo("/default"))
                .withHeader("Content-Type", equalToIgnoreCase("application/json"))
                .withRequestBody(equalTo(json)));
    }

    @Test
    public void gatewayConnectionShouldNotBeClosedOnWrongPayload() {
        Given("An endpoint with http backends and a backend server");
        var connectionId = UUID.randomUUID().toString();
        if (endpoint == null) {
            endpoint = EndpointFixtures.getRepresentation("/payloads/test2", httpRoutes);
            endpointsClient.createAndAssert(endpoint);
        }

        wiremock.stubFor(post(urlPathEqualTo("/default"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")));

        When("A WS connection is created and an invalid json payload is sent");
        var connection = webSocketClient.connect("/payloads/test2", connectionId);
        connection.send("{invalid");

        Then("Connection is not closed and still process events");
        connection.send(JsonUtils.toJson("hello"));
        Awaitility.await("backends should receive messages")
                .atMost(Duration.ofSeconds(15))
                .until(Conditions.receivedRequest(wiremock), greaterThan(1));

        wiremock.verify(1, postRequestedFor(urlPathEqualTo("/default"))
                .withHeader("Content-Type", equalToIgnoreCase("application/json"))
                .withRequestBody(equalTo(JsonUtils.toJson("hello"))));
    }
}
