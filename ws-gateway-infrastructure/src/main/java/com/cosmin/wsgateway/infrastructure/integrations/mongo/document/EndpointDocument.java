package com.cosmin.wsgateway.infrastructure.integrations.mongo.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class EndpointDocument {

    @Id
    private String id;

    private String path;

    private EndpointConfigurationDocument configuration;
}
