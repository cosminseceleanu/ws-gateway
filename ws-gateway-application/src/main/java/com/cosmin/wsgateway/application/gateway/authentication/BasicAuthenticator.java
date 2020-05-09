package com.cosmin.wsgateway.application.gateway.authentication;

import com.cosmin.wsgateway.domain.Authentication;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class BasicAuthenticator implements Authenticator<Authentication.Basic> {
    @Override
    public Mono<Boolean> isAuthenticated(Map<String, String> headers, Authentication.Basic authentication) {
        return Optional.ofNullable(headers.get("Authorization"))
                .map(rawHeader -> rawHeader.replace("Basic ", ""))
                .map(token -> token.equals(encodeBase64(authentication)))
                .map(Mono::just)
                .orElse(Mono.just(false));
    }

    private String encodeBase64(Authentication.Basic authentication) {
        var value = String.format("%s:%s", authentication.getUsername(), authentication.getPassword());
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Class<Authentication.Basic> supports() {
        return Authentication.Basic.class;
    }
}
