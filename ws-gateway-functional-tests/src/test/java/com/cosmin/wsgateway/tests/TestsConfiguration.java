package com.cosmin.wsgateway.tests;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestsConfiguration {
    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        return new NettyReactiveWebServerFactory();
    }
}
