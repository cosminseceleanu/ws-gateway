package com.cosmin.wsgateway.api.config;

import com.cosmin.wsgateway.application.gateway.GatewayProperties;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public Vertx vertx() {
        var vertxOptions = new VertxOptions();

        return Vertx.vertx(vertxOptions);
    }

    @Bean
    @ConfigurationProperties(prefix = "gateway")
    public GatewayProperties gatewayProperties() {
        return new GatewayProperties();
    }
}
