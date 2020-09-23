package com.cosmin.wsgateway.api.mappers;

import com.cosmin.wsgateway.api.representation.GeneralSettingsRepresentation;
import com.cosmin.wsgateway.domain.GeneralSettings;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
public class GeneralSettingsMapper implements RepresentationMapper<GeneralSettingsRepresentation, GeneralSettings> {

    private final GeneralSettingsMapper.SettingsMapper mapper = Mappers
            .getMapper(GeneralSettingsMapper.SettingsMapper.class);

    @Mapper
    public interface SettingsMapper {
        GeneralSettings toSettings(GeneralSettingsRepresentation representation);

        @InheritInverseConfiguration
        GeneralSettingsRepresentation fromSettings(GeneralSettings model);
    }

    @Override
    public GeneralSettings toModel(GeneralSettingsRepresentation representation) {
        if (representation == null) {
            return GeneralSettings.ofDefaults();
        }

        return mapper.toSettings(representation);
    }

    @Override
    public GeneralSettingsRepresentation toRepresentation(GeneralSettings domain) {
        return mapper.fromSettings(domain);
    }
}
