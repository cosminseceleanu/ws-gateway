package com.cosmin.wsgateway.infrastructure;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GatewayProperties {

    private Kubernetes kubernetes = new Kubernetes();

    private Vertx vertx = new Vertx();

    private GatewayAuthenticationHttpClient gatewayAuthenticationHttpClient = new GatewayAuthenticationHttpClient();

    private PubSub pubsub = new PubSub();

    @Data
    @NoArgsConstructor
    public static class Kubernetes {
        private String namespace;
        private String serviceName;
    }

    @Data
    public static class Vertx {
        private Integer gatewayVerticleInstances = 2;
        private Integer gatewayPort = 8081;
    }

    @Data
    public static class GatewayAuthenticationHttpClient {
        private Integer connectTimeout = 2000;
        private Integer readTimeout = 10000;
    }

    @Data
    public static class PubSub {
        private Boolean ignite = true;
        private Boolean hazelcast = false;
        private Boolean mocked = false;
    }
}
