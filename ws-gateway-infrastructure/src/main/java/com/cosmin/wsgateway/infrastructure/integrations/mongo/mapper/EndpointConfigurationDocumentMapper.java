package com.cosmin.wsgateway.infrastructure.integrations.mongo.mapper;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

import com.cosmin.wsgateway.domain.Authentication;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.EndpointConfiguration;
import com.cosmin.wsgateway.domain.GeneralSettings;
import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.backends.KafkaBackend;
import com.cosmin.wsgateway.infrastructure.common.ExpressionMapper;
import com.cosmin.wsgateway.infrastructure.integrations.mongo.document.EndpointConfigurationDocument;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EndpointConfigurationDocumentMapper {

    private final ExpressionMapper expressionMapper;

    public EndpointConfigurationDocument toDocument(EndpointConfiguration configuration) {
        GeneralSettings generalSettings = configuration.getGeneralSettings();

        return EndpointConfigurationDocument.builder()
                .settings(EndpointConfigurationDocument.Settings.builder()
                        .heartbeatMaxMissingPingFrames(generalSettings.getHeartbeatMaxMissingPingFrames())
                        .heartbeatIntervalInSeconds(generalSettings.getHeartbeatIntervalInSeconds())
                        .backendParallelism(generalSettings.getBackendParallelism())
                        .build())
                .authentication(mapToDocumentAuthentication(configuration))
                .filters(configuration.getFilters().stream().map(f -> EndpointConfigurationDocument.Filter.builder()
                        .name(f.name())
                        .value(f.value())
                        .build()).collect(Collectors.toSet()))
                .routes(mapToDocumentRoutes(configuration))
                .build();
    }

    private Set<EndpointConfigurationDocument.Route> mapToDocumentRoutes(EndpointConfiguration configuration) {
        return configuration.getRoutes().stream().map(r -> EndpointConfigurationDocument.Route.builder()
                .name(r.getName())
                .type(r.getType().name())
                .expression(r.getExpression().map(expressionMapper::toMap).orElse(new HashMap<>()))
                .backends(r.getBackends().stream().map(this::mapToDocumentBackend).collect(Collectors.toSet()))
                .build()).collect(Collectors.toSet());
    }

    private EndpointConfigurationDocument.Backend mapToDocumentBackend(Backend<?> backend) {
        var builder = EndpointConfigurationDocument.Backend.builder()
                .destination(backend.destination())
                .type(backend.type().name());

        Match<Backend<?>> match = Match(backend);

        return match.of(
                Case($(instanceOf(HttpBackend.class)), mapHttpBackend(builder)),
                Case($(instanceOf(KafkaBackend.class)), mapKafkaBackend(builder))
        );
    }

    private Function<? extends HttpBackend, EndpointConfigurationDocument.Backend> mapHttpBackend(
            EndpointConfigurationDocument.Backend.BackendBuilder builder) {
        return httpBackend -> builder
                .httpAdditionalHeaders(httpBackend.getSettings().getAdditionalHeaders())
                .httpConnectTimeoutInMillis(httpBackend.getSettings().getConnectTimeoutInMillis())
                .httpReadTimeoutInMillis(httpBackend.getSettings().getReadTimeoutInMillis())
                .build();
    }

    private Function<? extends KafkaBackend, EndpointConfigurationDocument.Backend> mapKafkaBackend(
            EndpointConfigurationDocument.Backend.BackendBuilder builder) {
        return kafkaBackend -> builder
                .kafkaAcks(kafkaBackend.getSettings().getAcks().name())
                .kafkaRetriesNr(kafkaBackend.getSettings().getRetriesNr())
                .kafkaBootstrapServers(kafkaBackend.getSettings().getBootstrapServers())
                .build();
    }

    private EndpointConfigurationDocument.Authentication mapToDocumentAuthentication(
            EndpointConfiguration configuration) {
        var builder = EndpointConfigurationDocument.Authentication.builder();
        builder.className(configuration.getAuthentication().getClass().getName());

        return Match(configuration.getAuthentication()).of(
                Case($(instanceOf(Authentication.Basic.class)), a -> builder
                        .username(a.getUsername())
                        .password(a.getPassword())
                        .build()),
                Case($(instanceOf(Authentication.Bearer.class)), a -> builder
                        .authorizationServerUrl(a.getAuthorizationServerUrl())
                        .build()),
                Case($(instanceOf(Authentication.None.class)), a -> builder.build())
        );
    }

    public EndpointConfiguration toDomainObject(EndpointConfigurationDocument document) {
        EndpointConfigurationDocument.Authentication auth = document.getAuthentication();
        return EndpointConfiguration.builder()
                .createdAt(document.getCreatedAt())
                .authentication(Match(auth.getClassName()).of(
                        Case($(Authentication.None.class.getName()), new Authentication.None()),
                        Case($(Authentication.Bearer.class.getName()), new Authentication.Bearer(auth.getAuthorizationServerUrl())),
                        Case($(Authentication.None.class.getName()), new Authentication.Basic(auth.getUsername(), auth.getPassword()))
                )))
                .build();
    }
}
