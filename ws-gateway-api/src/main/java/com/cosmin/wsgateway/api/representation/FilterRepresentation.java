package com.cosmin.wsgateway.api.representation;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Collections;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterRepresentation {
    private Set<String> whitelistIps = Collections.emptySet();
    private Set<String> whitelistHosts = Collections.emptySet();
    private Set<String> blacklistIps = Collections.emptySet();
    private Set<String> blacklistHosts = Collections.emptySet();
}
