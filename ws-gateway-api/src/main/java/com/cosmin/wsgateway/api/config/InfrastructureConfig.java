package com.cosmin.wsgateway.api.config;

import com.cosmin.infrastructure.persistence.InMemoryEndpointRepository;
import com.cosmin.wsgateway.api.repositories.EndpointRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {

    @Bean
    public EndpointRepository endpointRepository() {
        return new InMemoryEndpointRepository();
    }
}
