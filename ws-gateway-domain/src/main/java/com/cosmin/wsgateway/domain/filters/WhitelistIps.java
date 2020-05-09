package com.cosmin.wsgateway.domain.filters;

import com.cosmin.wsgateway.domain.Filter;
import java.util.Map;
import java.util.Set;
import lombok.Value;

@Value
public class WhitelistIps implements Filter<Set<String>> {
    private final Set<String> ips;

    @Override
    public String name() {
        return "whitelistIps";
    }

    @Override
    public Set<String> value() {
        return ips;
    }

    @Override
    public boolean isAllowed(Map<String, String> requestHeaders) {
        if (ips.isEmpty()) {
            return true;
        }
        return requestHeaders.containsKey("X_FORWARDED_FOR") && ips.contains(requestHeaders.get("X_FORWARDED_FOR"));
    }
}
