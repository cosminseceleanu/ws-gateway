package com.cosmin.wsgateway.application.gateway.connection.filters;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

import com.cosmin.wsgateway.application.gateway.authentication.Authenticator;
import com.cosmin.wsgateway.application.gateway.connection.ConnectionRequest;
import com.cosmin.wsgateway.application.gateway.exceptions.AuthenticationException;
import com.cosmin.wsgateway.domain.Authentication;
import com.cosmin.wsgateway.domain.Endpoint;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements ConnectionFilter {
    private final List<Authenticator<? extends Authentication>> checkers;

    @Override
    public Mono<ConnectionRequest> filter(Endpoint endpoint, ConnectionRequest request) {
        log.info("Check auth for {} with {}",
                keyValue("path", endpoint.getPath()),
                keyValue("auth", endpoint.getAuthentication().getClass().getName())
        );

        return checkers.stream()
                .filter(c -> c.supports().isAssignableFrom(endpoint.getAuthentication().getClass()))
                .findFirst()
                .map(authenticator -> checkAuth(endpoint, request, (Authenticator<Authentication>) authenticator))
                .orElse(Mono.error(new AuthenticationException()))
                .map(r -> request);
    }

    private Mono<Boolean> checkAuth(
            Endpoint endpoint, ConnectionRequest request, Authenticator<Authentication> authenticator
    ) {
        return authenticator.isAuthenticated(request.getHeaders(), endpoint.getAuthentication())
        .map(isAuthenticated -> {
            if (!isAuthenticated) {
                throw new AuthenticationException("Bad Credentials");
            }
            return true;
        });
    }
}
