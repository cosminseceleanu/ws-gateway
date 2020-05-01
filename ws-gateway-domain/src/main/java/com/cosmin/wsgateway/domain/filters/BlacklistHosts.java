package com.cosmin.wsgateway.domain.filters;

import com.cosmin.wsgateway.domain.Filter;
import java.util.Map;
import java.util.Set;
import lombok.Value;

@Value
public class BlacklistHosts implements Filter<Set<String>> {
    private final Set<String> hosts;

    @Override
    public String name() {
        return "blacklistHosts";
    }

    @Override
    public Set<String> value() {
        return hosts;
    }

    @Override
    public boolean isAllowed(Map<String, String> requestHeaders) {
        return !requestHeaders.containsKey("Host") || !hosts.contains(requestHeaders.get("Host"));
    }
}
