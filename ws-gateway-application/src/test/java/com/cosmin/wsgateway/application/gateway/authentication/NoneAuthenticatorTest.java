package com.cosmin.wsgateway.application.gateway.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cosmin.wsgateway.domain.Authentication;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class NoneAuthenticatorTest {
    private final NoneAuthenticator subject = new NoneAuthenticator();

    @Test
    public void testIsAuthenticated() {
        var result = subject.isAuthenticated(Map.of(), new Authentication.None());

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void testSupports() {
        assertEquals(Authentication.None.class, subject.supports());
    }
}