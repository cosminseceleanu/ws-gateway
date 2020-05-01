package com.cosmin.wsgateway.application.gateway.authentication;

import com.cosmin.wsgateway.domain.Authentication;
import java.util.Map;
import reactor.core.publisher.Mono;

public interface Authenticator<T extends Authentication> {
    Mono<Boolean> isAuthenticated(Map<String, String> headers, T authentication);

    Class<T> supports();
}
