package com.cosmin.wsgateway.application.gateway.connection;

import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Value
public class ConnectionRequest {
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final String path;
}
