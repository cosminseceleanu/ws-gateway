package com.cosmin.wsgateway.infrastructure.integrations.mongo.mapper;

import static com.cosmin.wsgateway.infrastructure.fixtures.ExpressionFixtures.createTerminalExpression;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.cosmin.wsgateway.domain.Authentication;
import com.cosmin.wsgateway.domain.EndpointConfiguration;
import com.cosmin.wsgateway.domain.Expression;
import com.cosmin.wsgateway.domain.GeneralSettings;
import com.cosmin.wsgateway.domain.Route;
import com.cosmin.wsgateway.domain.backends.HttpBackend;
import com.cosmin.wsgateway.domain.backends.HttpSettings;
import com.cosmin.wsgateway.domain.backends.KafkaBackend;
import com.cosmin.wsgateway.domain.expressions.Expressions;
import com.cosmin.wsgateway.domain.filters.BlacklistHosts;
import com.cosmin.wsgateway.infrastructure.common.ExpressionMapper;
import com.cosmin.wsgateway.infrastructure.fixtures.ExpressionFixtures;
import com.cosmin.wsgateway.infrastructure.integrations.mongo.document.EndpointConfigurationDocument;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EndpointConfigurationDocumentMapperTest {

    private final EndpointConfigurationDocument defaultDocument = EndpointConfigurationDocument
            .builder()
            .settings(EndpointConfigurationDocument.Settings.builder()
                    .backendParallelism(GeneralSettings.DEFAULT_BACKEND_PARALLELISM)
                    .heartbeatIntervalInSeconds(GeneralSettings.DEFAULT_HEARTBEAT_INTERVAL_IN_SECONDS)
                    .heartbeatMaxMissingPingFrames(GeneralSettings.DEFAULT_HEARTBEAT_MAX_MISSING_PING_FRAMES)
                    .build())
            .filters(Set.of(EndpointConfigurationDocument.Filter.builder()
                    .name("blacklistHosts")
                    .value(Set.of("h1", "h2"))
                    .build()))
            .routes(Set.of(EndpointConfigurationDocument.Route.builder()
                    .type("DEFAULT")
                    .backends(Set.of(
                            EndpointConfigurationDocument.Backend.builder()
                                    .destination("localhost")
                                    .type("HTTP")
                                    .httpConnectTimeoutInMillis(HttpSettings.DEFAULT_CONNECT_TIMEOUT_IN_MILLIS)
                                    .httpReadTimeoutInMillis(HttpSettings.READ_TIMEOUT_IN_MILLIS)
                                    .httpAdditionalHeaders(new HashMap<>())
                                    .build()
                    ))
                    .expression(new HashMap<>())
                    .name("Default")
                    .build()))
            .authentication(EndpointConfigurationDocument.Authentication.builder()
                    .className(Authentication.Basic.class.getName())
                    .username("u")
                    .password("p")
                    .build())
            .build();

    @Mock
    private ExpressionMapper expressionMapper;

    @InjectMocks
    private EndpointConfigurationDocumentMapper subject;

    @Test
    public void testToDocument_givenDomainEndpointConfiguration_shouldMapAllFieldsToMongoDocument() {
        EndpointConfiguration endpointConfiguration = EndpointConfiguration.ofRoutes(Set.of(
                Route.defaultRoute(Set.of(HttpBackend.ofDefaults("localhost")))
        )).withFilters(Set.of(new BlacklistHosts(Set.of("h1", "h2"))))
                .withAuthentication(new Authentication.Basic("u", "p"));

        var result = subject.toDocument(endpointConfiguration);

        assertEquals(defaultDocument, result);
    }

    @Test
    public void testToDocument_givenConfigurationWithKafkaBackend_shouldMapKafkaBackend() {
        EndpointConfiguration endpointConfiguration = EndpointConfiguration.ofRoutes(Set.of(
                Route.defaultRoute(Set.of(KafkaBackend.ofDefaults("topic", "localhost")))
        ));

        var result = subject.toDocument(endpointConfiguration);
        var backend = result.getRoutes()
                .stream()
                .flatMap(r -> r.getBackends().stream())
                .findFirst()
                .get();
        assertEquals("topic", backend.getDestination());
        assertEquals("localhost", backend.getKafkaBootstrapServers());
        assertEquals("KAFKA", backend.getType());
        assertEquals("LEADER", backend.getKafkaAcks());
        assertEquals(1, backend.getKafkaRetriesNr());
    }

    @Test
    public void testToDocument_givenConfiguration_shouldMapRouteExpression() {
        Expression<Boolean> expression = Expressions.equal("e", "$.*");
        EndpointConfiguration endpointConfiguration = EndpointConfiguration.ofRoutes(Set.of(
                Route.defaultRoute().withExpression(Optional.of(expression))
        ));
        when(expressionMapper.toMap(expression)).thenReturn(createTerminalExpression("$.*", "e", "equals"));

        var result = subject.toDocument(endpointConfiguration);
        var route = result.getRoutes()
                .stream()
                .findFirst()
                .get();
        assertEquals(createTerminalExpression("$.*", "e", "equals"), route.getExpression());
    }

    @Test
    public void testToDomainObject_givenMongoDocument_shouldMapAllFieldsToDomainModel() {

        var result = subject.toDomainObject(defaultDocument);

        var expected = EndpointConfiguration.ofRoutes(Set.of(
                Route.defaultRoute(Set.of(HttpBackend.ofDefaults("localhost")))
        )).withFilters(Set.of(new BlacklistHosts(Set.of("h1", "h2"))))
                .withAuthentication(new Authentication.Basic("u", "p"));

        assertEquals(expected, result);
    }
}