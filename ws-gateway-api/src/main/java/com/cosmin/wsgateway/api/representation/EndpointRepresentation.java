package com.cosmin.wsgateway.api.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EndpointRepresentation {
    private String id;
    private String path;

    @JsonProperty("settings")
    private GeneralSettingsRepresentation generalSettings;
    private FilterRepresentation filters = new FilterRepresentation();
    private Set<RouteRepresentation> routes;
    private AuthenticationRepresentation authentication;
}
