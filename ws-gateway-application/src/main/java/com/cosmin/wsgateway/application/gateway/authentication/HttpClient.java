package com.cosmin.wsgateway.application.gateway.authentication;

import reactor.core.publisher.Mono;

public interface HttpClient {
    Mono<Integer> checkToken(String url, String token);
}
