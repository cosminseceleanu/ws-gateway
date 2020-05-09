package com.cosmin.wsgateway.application.gateway.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import com.cosmin.wsgateway.domain.Authentication;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BearerAuthenticatorTest {

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private BearerAuthenticator subject;

    private Authentication.Bearer authentication = new Authentication.Bearer("serverUrl");

    @Test
    public void testIsAuthenticated_whenHeaderIsMissing_thenNotAuthenticated() {
        var result = subject.isAuthenticated(Collections.emptyMap(), authentication);
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void testIsAuthenticated_whenTokenIsNotValid_thenNotAuthenticated() {
        doReturn(Mono.just(401)).when(httpClient).checkToken("serverUrl", "token");

        var result = subject.isAuthenticated(Map.of("Authorization", "Bearer token"), authentication);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void testIsAuthenticated_whenHttpRequestFails_thenNotAuthenticated() {
        doReturn(Mono.error(new RuntimeException("test"))).when(httpClient).checkToken("serverUrl", "token");

        var result = subject.isAuthenticated(Map.of("Authorization", "Bearer token"), authentication);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void testIsAuthenticated() {
        doReturn(Mono.just(200)).when(httpClient).checkToken("serverUrl", "token");

        var result = subject.isAuthenticated(Map.of("Authorization", "Bearer token"), authentication);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void testSupports() {
        assertEquals(Authentication.Bearer.class, subject.supports());
    }
}