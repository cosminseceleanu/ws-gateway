package com.cosmin.wsgateway.domain.model.filters;

import com.cosmin.wsgateway.domain.model.Filter;

import lombok.Value;

import java.util.Map;
import java.util.Set;

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
