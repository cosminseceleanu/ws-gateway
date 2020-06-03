package com.cosmin.wsgateway.api.mappers;

import com.cosmin.wsgateway.api.representation.BackendRepresentation;
import com.cosmin.wsgateway.api.representation.HttpBackendRepresentation;
import com.cosmin.wsgateway.api.representation.KafkaBackendRepresentation;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.backends.KafkaBackend;
import com.cosmin.wsgateway.domain.exceptions.BackendNotSupportedException;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;


@Component
public class BackendMapper implements RepresentationMapper<BackendRepresentation, Backend<? extends BackendSettings>> {
    private final HttpBackendMapper httpBackendMapper = Mappers.getMapper(HttpBackendMapper.class);
    private final KafkaBackendMapper kafkaBackendMapper = Mappers.getMapper(KafkaBackendMapper.class);

    @Mapper
    public interface HttpBackendMapper {
        @Mapping(target = "settings.readTimeoutInMillis", source = "readTimeoutInMillis")
        @Mapping(target = "settings.connectTimeoutInMillis", source = "connectTimeoutInMillis")
        @Mapping(target = "settings.additionalHeaders", source = "additionalHeaders")
        HttpBackend toHttpBackend(HttpBackendRepresentation representation);

        @InheritInverseConfiguration
        HttpBackendRepresentation fromHttpBackend(HttpBackend model);
    }

    @Mapper
    public interface KafkaBackendMapper {
        @Mapping(target = "settings.bootstrapServers", source = "bootstrapServers")
        @Mapping(target = "settings.retriesNr", source = "retriesNr")
        @Mapping(target = "settings.acks", source = "acks")
        KafkaBackend toKafkaBackend(KafkaBackendRepresentation representation);

        @InheritInverseConfiguration
        KafkaBackendRepresentation fromKafkaBackend(KafkaBackend model);
    }

    @Override
    public Backend<? extends BackendSettings> toModel(BackendRepresentation representation) {
        if (representation instanceof HttpBackendRepresentation) {
            return httpBackendMapper.toHttpBackend((HttpBackendRepresentation) representation);
        }
        if (representation instanceof KafkaBackendRepresentation) {
            return kafkaBackendMapper.toKafkaBackend((KafkaBackendRepresentation) representation);
        }

        throw new BackendNotSupportedException(representation.getClass().getName());
    }

    @Override
    public BackendRepresentation toRepresentation(Backend<? extends BackendSettings> domain) {
        if (domain instanceof HttpBackend) {
            return httpBackendMapper.fromHttpBackend((HttpBackend) domain);
        }
        if (domain instanceof KafkaBackend) {
            return kafkaBackendMapper.fromKafkaBackend((KafkaBackend) domain);
        }

        throw new BackendNotSupportedException(domain.getClass().getName());
    }
}
