package com.cosmin.wsgateway.api.rest.mappers;

import com.cosmin.wsgateway.api.rest.representation.HttpBackendRepresentation;
import com.cosmin.wsgateway.domain.model.backends.HttpBackend;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


public class HttpBackendMapper implements RepresentationMapper<HttpBackendRepresentation, HttpBackend> {
    private final MapStructMapper mapper = Mappers.getMapper(MapStructMapper.class);

    @Mapper
    public interface MapStructMapper {
        @Mapping(target = "settings.timeoutInMillis", source = "timeoutInMillis")
        @Mapping(target = "settings.additionalHeaders", source = "additionalHeaders")
        HttpBackend toHttpBackend(HttpBackendRepresentation representation);

        @InheritInverseConfiguration
        HttpBackendRepresentation fromHttpBackend(HttpBackend model);

    }

    @Override
    public HttpBackend toModel(HttpBackendRepresentation representation) {
        return mapper.toHttpBackend(representation);
    }

    @Override
    public HttpBackendRepresentation toRepresentation(HttpBackend domain) {
        return mapper.fromHttpBackend(domain);
    }
}
