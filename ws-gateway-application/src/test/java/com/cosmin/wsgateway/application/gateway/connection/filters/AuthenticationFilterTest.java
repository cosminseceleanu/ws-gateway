package com.cosmin.wsgateway.application.gateway.connection.filters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cosmin.wsgateway.application.Fixtures;
import com.cosmin.wsgateway.application.gateway.authentication.Authenticator;
import com.cosmin.wsgateway.application.gateway.connection.ConnectionRequest;
import com.cosmin.wsgateway.application.gateway.exceptions.AuthenticationException;
import com.cosmin.wsgateway.domain.Authentication;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Test
    public void testFilter_multipleAuthenticators_shouldUseFirstSupported() {
        var auth1 = mock(Authenticator.class);
        when(auth1.supports()).thenReturn(Authentication.None.class);

        var auth2 = mock(Authenticator.class);
        when(auth2.supports()).thenReturn(Authentication.Basic.class);
        when(auth2.isAuthenticated(anyMap(), any())).thenReturn(Mono.just(true));


        var subject = new AuthenticationFilter(List.of(auth1, auth2));
        var endpoint = Fixtures.defaultEndpoint();
        endpoint = endpoint.withConfiguration(endpoint.getConfiguration()
                .withAuthentication(new Authentication.Basic("a","b")));

        subject.filter(endpoint, ConnectionRequest.builder()
                .headers(Collections.emptyMap())
                .build()).block();
        verify(auth1, never()).isAuthenticated(anyMap(), any());
        verify(auth2, times(1)).isAuthenticated(anyMap(), any());
    }

    @Test
    public void testFilter_isAuthenticated_filterShouldPass() {
        var auth1 = mock(Authenticator.class);
        when(auth1.supports()).thenReturn(Authentication.Basic.class);
        when(auth1.isAuthenticated(anyMap(), any())).thenReturn(Mono.just(true));


        var subject = new AuthenticationFilter(List.of(auth1));
        var endpoint = Fixtures.defaultEndpoint();
        endpoint = endpoint.withConfiguration(endpoint.getConfiguration()
                .withAuthentication(new Authentication.Basic("a","b")));

        ConnectionRequest request = ConnectionRequest.builder()
                .headers(Collections.emptyMap())
                .build();
        var result = subject.filter(endpoint, request);
        StepVerifier.create(result)
                .expectNext(request)
                .verifyComplete();
    }

    @Test
    public void testFilter_isNotAuthenticated_shouldThrowException() {
        var auth1 = mock(Authenticator.class);
        when(auth1.supports()).thenReturn(Authentication.Basic.class);
        when(auth1.isAuthenticated(anyMap(), any())).thenReturn(Mono.just(false));


        var subject = new AuthenticationFilter(List.of(auth1));
        var endpoint = Fixtures.defaultEndpoint();
        endpoint = endpoint.withConfiguration(endpoint.getConfiguration()
                .withAuthentication(new Authentication.Basic("a","b")));

        ConnectionRequest request = ConnectionRequest.builder()
                .headers(Collections.emptyMap())
                .build();
        var result = subject.filter(endpoint, request);
        StepVerifier.create(result)
                .expectError(AuthenticationException.class)
                .verify();
    }

    @Test
    public void testFilter_missingAuthenticator_shouldFailWithAccessDenied() {
        var subject = new AuthenticationFilter(Collections.emptyList());
        var endpoint = Fixtures.defaultEndpoint();

        ConnectionRequest request = ConnectionRequest.builder().build();
        var result = subject.filter(endpoint, request);

        StepVerifier.create(result)
                .expectError(AuthenticationException.class)
                .verify();
    }
}