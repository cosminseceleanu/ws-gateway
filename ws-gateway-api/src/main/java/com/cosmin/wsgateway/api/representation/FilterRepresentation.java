package com.cosmin.wsgateway.api.representation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRepresentation {
    private Set<String> whitelistIps = Collections.emptySet();
    private Set<String> whitelistHosts = Collections.emptySet();
    private Set<String> blacklistIps = Collections.emptySet();
    private Set<String> blacklistHosts = Collections.emptySet();
}
