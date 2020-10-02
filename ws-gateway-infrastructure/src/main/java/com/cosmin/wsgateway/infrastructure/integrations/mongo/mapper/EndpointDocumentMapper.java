package com.cosmin.wsgateway.infrastructure.integrations.mongo.mapper;

import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.infrastructure.integrations.mongo.document.EndpointDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EndpointDocumentMapper {
    private final EndpointConfigurationDocumentMapper configurationMapper;

    public EndpointDocument toDocument(Endpoint endpoint) {
        return EndpointDocument.builder()
                .configuration(configurationMapper.toDocument(endpoint.getConfiguration()))
                .path(endpoint.getPath())
                .id(endpoint.getId())
                .build();
    }

    public Endpoint toDomainObject(EndpointDocument document) {
        return Endpoint.builder()
                .configuration(configurationMapper.toDomainObject(document.getConfiguration()))
                .path(document.getPath())
                .id(document.getId())
                .build();
    }
}
