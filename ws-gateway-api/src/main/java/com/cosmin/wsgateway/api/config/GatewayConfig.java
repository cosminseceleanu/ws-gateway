package com.cosmin.wsgateway.api.config;

import com.cosmin.infrastructure.gateway.JacksonMessageTransformer;
import com.cosmin.infrastructure.gateway.MockedPubSub;
import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.PubSub;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public PayloadTransformer messageTransformer(ObjectMapper objectMapper) {
        return new JacksonMessageTransformer(objectMapper);
    }

    @Bean
    public PubSub pubSub() {
        return new MockedPubSub();
    }
}
