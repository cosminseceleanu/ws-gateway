package com.cosmin.wsgateway.application.gateway.connection;

import lombok.Value;

import java.util.Map;

@Value
public class ConnectionRequest {
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final String path;
}
