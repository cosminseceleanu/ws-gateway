package com.cosmin.wsgateway.application.gateway.authentication;

import com.cosmin.wsgateway.domain.Authentication;
import java.util.Map;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class NoneAuthenticator implements Authenticator<Authentication.None> {
    @Override
    public Mono<Boolean> isAuthenticated(Map<String, String> headers, Authentication.None authentication) {
        return Mono.just(true);
    }

    @Override
    public Class<Authentication.None> supports() {
        return Authentication.None.class;
    }
}
