package com.cosmin.wsgateway.api.rest.representation;

import lombok.Data;

import java.util.Set;

@Data
public class FilterRepresentation {
    private Set<String> whitelistIps;
    private Set<String> whitelistHosts;
    private Set<String> blacklistIps;
    private Set<String> blacklistHosts;
}
