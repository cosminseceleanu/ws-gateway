package com.cosmin.wsgateway.infrastructure.integrations.mongo.document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EndpointConfigurationDocument {

    private Set<Filter> filters;
    private Authentication authentication;
    private Settings settings;
    private Set<Route> routes;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class Filter {
        private String name;
        private Object value;
    }

    @Data
    @Builder
    public static class Authentication {
        private String className;
        private String username;
        private String password;
        private String authorizationServerUrl;
    }

    @Data
    @Builder
    public static class Settings {
        private Integer backendParallelism;
        private Integer heartbeatIntervalInSeconds;
        private Integer heartbeatMaxMissingPingFrames;
    }

    @Data
    @Builder
    public static class Route {
        private String type;
        private String name;
        private Set<Backend> backends;

        private Map<String, Object> expression;
    }

    @Data
    @Builder
    public static class Backend {
        private String type;
        private String destination;

        private Map<String, String> httpAdditionalHeaders;
        private Integer httpReadTimeoutInMillis;
        private Integer httpConnectTimeoutInMillis;

        private String kafkaBootstrapServers;
        private String kafkaAcks;
        private Integer kafkaRetriesNr;

    }
}
