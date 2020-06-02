package com.cosmin.wsgateway.infrastructure.gateway.connectors;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import com.cosmin.wsgateway.application.gateway.connection.events.BackendErrorEvent;
import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.backends.HttpSettings;
import com.cosmin.wsgateway.domain.events.Connected;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Fault;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class HttpConnectorTest {
    private static final WireMockServer wiremock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    private static String wiremockUrl;

    private final HttpBackend backend = HttpBackend.builder()
            .destination(wiremockUrl + "/test")
            .settings(HttpSettings.builder()
                    .additionalHeaders(Map.of(
                            "X-Debug-Id", "my-debug-id"
                    ))
                    .timeoutInMillis(200)
                    .build())
            .build();

    @InjectMocks
    private HttpConnector subject;

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
    public void testSendEvent_eventIsSuccessfulSent_shouldReturnInitialEvent() {
        var event = new Connected("1233455");
        wiremock.stubFor(post(urlPathEqualTo("/test"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.NO_CONTENT.value())
                ));

        var result = subject.sendEvent(event, backend);

        StepVerifier.create(result)
                .expectNext(event)
                .verifyComplete();

        wiremock.verify(1, postRequestedFor(urlPathEqualTo("/test"))
                .withHeader("Content-Type", equalToIgnoreCase("application/json"))
                .withHeader("X-Debug-Id", equalTo("my-debug-id"))
                .withRequestBody(equalTo("{\"connectionId\":\"1233455\"}")));
    }

    @Test
    public void testSendEvent_requestToBackendReturn404_shouldReturnAnErrorEvent() {
        var event = new Connected("1233455");
        wiremock.stubFor(post(urlPathEqualTo("/test"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.NOT_FOUND.value())
                ));

        var result = subject.sendEvent(event, backend);

        StepVerifier.create(result)
                .expectNextMatches(e -> e instanceof BackendErrorEvent)
                .verifyComplete();
    }

    @Test
    public void testSendEvent_requestToBackendReturn50X_shouldReturnAnErrorEvent() {
        var event = new Connected("1233455");
        wiremock.stubFor(post(urlPathEqualTo("/test"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                ));

        var result = subject.sendEvent(event, backend);

        StepVerifier.create(result)
                .expectNextMatches(e -> e instanceof BackendErrorEvent)
                .verifyComplete();
    }

    @Test
    public void testSendEvent_whenHttpConnectionFails_shouldReturnAnErrorEvent() {
        var event = new Connected("1233455");
        wiremock.stubFor(post(urlPathEqualTo("/test"))
                .willReturn(aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)
                ));

        var result = subject.sendEvent(event, backend);

        StepVerifier.create(result)
                .expectNextMatches(e -> e instanceof BackendErrorEvent)
                .verifyComplete();
    }

    @Test
    public void testSendEvent_backendDoesNotRespondInExpectedTimeout_shouldReturnAnErrorEvent() {
        var event = new Connected("1233455");
        wiremock.stubFor(post(urlPathEqualTo("/test"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HttpStatus.NO_CONTENT.value())
                        .withFixedDelay(10000)
                ));

        var result = subject.sendEvent(event, backend);

        StepVerifier.create(result)
                .expectNextMatches(e -> e instanceof BackendErrorEvent)
                .verifyComplete();
    }
}