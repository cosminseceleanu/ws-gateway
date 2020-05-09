package com.cosmin.wsgateway.application.gateway.authentication;

import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.domain.Authentication;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class BasicAuthenticatorTest {
    private final Authentication.Basic basicAuthentication = new Authentication.Basic("user", "pass");
    private final BasicAuthenticator subject = new BasicAuthenticator();

    @Test
    public void testIsAuthenticated_whenHeaderIsMissing_thenNotAuthenticated() {
        var result = subject.isAuthenticated(Collections.emptyMap(), basicAuthentication);
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void testIsAuthenticated_whenHeaderIsWrong_thenNotAuthenticated() {
        var result = subject.isAuthenticated(Map.of("Authorization", "asda9uhbjh8y7yhb"), basicAuthentication);
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void testIsAuthenticated() {
        var value = String.format("%s:%s", "user", "pass");
        var token = Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));

        var result = subject.isAuthenticated(Map.of("Authorization", token), basicAuthentication);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void testSupports() {
        assertEquals(Authentication.Basic.class, subject.supports());
    }
}