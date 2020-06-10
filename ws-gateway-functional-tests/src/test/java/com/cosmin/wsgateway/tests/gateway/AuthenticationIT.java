package com.cosmin.wsgateway.tests.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cosmin.wsgateway.api.representation.AuthenticationRepresentation;
import com.cosmin.wsgateway.tests.BaseTestIT;
import com.cosmin.wsgateway.tests.Tags;
import com.cosmin.wsgateway.tests.client.WebSocketConnectionException;
import com.cosmin.wsgateway.tests.common.EndpointFixtures;
import com.cosmin.wsgateway.tests.common.JsonUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tags.Gateway
public class AuthenticationIT extends BaseTestIT {
    public static WireMockServer wiremock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    private static String wiremockUrl;

    @BeforeAll
    static void beforeAll() {
        wiremock.start();
        wiremockUrl = String.format("http://localhost:%d", wiremock.port());
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
    public void basicAuthenticationIsUsedAndChecked() {
        Given("endpoint with basic auth");
        var endpoint = EndpointFixtures.getRepresentation("/basic-auth");
        endpoint.setAuthentication(new AuthenticationRepresentation.Basic("username", "pass%6h"));
        endpointsClient.createAndAssert(endpoint);

        When("Connect with wrong auth");
        var thrown = assertThrows(WebSocketConnectionException.class, () -> {
            webSocketClient.connect("/basic-auth", Map.of("Authorization", "Basic wrongCredentials"));
        });
        Then("Connection is rejected with 401");
        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getHttpStatus());

        When("Connect with good auth");
        var goodToken = Base64.getEncoder().encodeToString("username:pass%6h".getBytes(StandardCharsets.UTF_8));
        Then("Connection is accepted");
        webSocketClient.connect("/basic-auth", Map.of("Authorization", "Basic " + goodToken));
    }

    @Test
    public void bearerAuthenticationIsUsedAndChecked() {
        Given("endpoint with bearer auth and an authorization server");
        var endpoint = EndpointFixtures.getRepresentation("/bearer-auth");
        endpoint.setAuthentication(new AuthenticationRepresentation.Bearer(wiremockUrl + "/verify_token"));
        endpointsClient.createAndAssert(endpoint);

        var validTokenPayload = JsonUtils.toJson(Map.of("access_token", "validToken"));
        var invalidTokenPayload = JsonUtils.toJson(Map.of("access_token", "invalidToken"));

        wiremock.stubFor(post(urlPathEqualTo("/verify_token"))
                .withRequestBody(equalTo(validTokenPayload))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")));
        wiremock.stubFor(post(urlPathEqualTo("/verify_token"))
                .withRequestBody(equalTo(invalidTokenPayload))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.FORBIDDEN.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")));


        When("Connect with wrong auth token");
        var thrown = assertThrows(WebSocketConnectionException.class, () -> {
            webSocketClient.connect(endpoint.getPath(), Map.of("Authorization", "Bearer invalidToken"));
        });
        Then("Connection is rejected with 401");
        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getHttpStatus());

        When("Connect with good auth token");
        webSocketClient.connect(endpoint.getPath(), Map.of("Authorization", "Bearer validToken"));
        Then("Connection is accepted");
    }
}
