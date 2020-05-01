package com.cosmin.wsgateway.application.gateway.authentication;

import com.cosmin.wsgateway.domain.Authentication;
import java.util.Map;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class BasicAuthenticator implements Authenticator<Authentication.Basic> {
    @Override
    public Mono<Boolean> isAuthenticated(Map<String, String> headers, Authentication.Basic authentication) {
        return Mono.just(false);
    }

    @Override
    public Class<Authentication.Basic> supports() {
        return Authentication.Basic.class;
    }
}
