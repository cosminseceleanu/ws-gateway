package com.cosmin.wsgateway.application.gateway;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GatewayProperties {

    private Kubernetes kubernetes = new Kubernetes();

    @Data
    @NoArgsConstructor
    public static class Kubernetes {
        private String namespace;
        private String serviceName;
    }
}
