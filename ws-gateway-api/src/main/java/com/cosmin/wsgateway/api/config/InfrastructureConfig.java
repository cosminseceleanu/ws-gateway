package com.cosmin.wsgateway.api.config;

import com.cosmin.wsgateway.application.configuration.repositories.EndpointRepository;
import com.cosmin.wsgateway.infrastructure.persistence.InMemoryEndpointRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {

    @Bean
    public EndpointRepository endpointRepository() {
        return new InMemoryEndpointRepository();
    }
}
