package com.cosmin.wsgateway.application.gateway;

import reactor.core.publisher.Flux;

public interface PubSub {
    Flux<String> subscribe(String topic);
}
