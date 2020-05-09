package com.cosmin.wsgateway.application.gateway.authentication;

import com.cosmin.wsgateway.domain.Authentication;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BearerAuthenticator implements Authenticator<Authentication.Bearer> {
    private final HttpClient httpClient;

    @Override
    public Mono<Boolean> isAuthenticated(Map<String, String> headers, Authentication.Bearer authentication) {
        return Optional.ofNullable(headers.get("Authorization"))
                .map(rawHeader -> rawHeader.replace("Bearer ", ""))
                .map(token -> httpClient.checkToken(authentication.getAuthorizationServerUrl(), token))
                .orElse(Mono.just(500))
                .map(this::isSuccessful)
                .onErrorReturn(false);
    }

    private boolean isSuccessful(Integer status) {
        return status >= 200 && status < 400;
    }

    @Override
    public Class<Authentication.Bearer> supports() {
        return Authentication.Bearer.class;
    }
}
