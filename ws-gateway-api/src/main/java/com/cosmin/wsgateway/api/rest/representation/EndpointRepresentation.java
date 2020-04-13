package com.cosmin.wsgateway.api.rest.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class EndpointRepresentation {
    private String id;
    private String path;
    private FilterRepresentation filters;
    private Set<RouteRepresentation> routes;
    private AuthenticationRepresentation authentication;
}
