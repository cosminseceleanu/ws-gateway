package com.cosmin.wsgateway.api.representation;

import java.util.Collections;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRepresentation {
    private Set<String> whitelistIps = Collections.emptySet();
    private Set<String> whitelistHosts = Collections.emptySet();
    private Set<String> blacklistIps = Collections.emptySet();
    private Set<String> blacklistHosts = Collections.emptySet();
}
