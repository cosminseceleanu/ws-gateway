package com.cosmin.wsgateway.application.gateway.connection;

import java.util.Map;
import lombok.Value;

@Value
public class ConnectionRequest {
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final String path;
}
