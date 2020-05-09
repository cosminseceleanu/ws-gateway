package com.cosmin.wsgateway.application.configuration.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import com.cosmin.wsgateway.application.Fixtures;
import com.cosmin.wsgateway.application.configuration.repositories.EndpointRepository;
import com.cosmin.wsgateway.domain.Authentication;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.EndpointConfiguration;
import com.cosmin.wsgateway.domain.Route;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class EndpointWriterTest {
    @Mock
    private EndpointRepository endpointRepository;

    @Mock
    private EndpointsProvider endpointsProvider;

    @InjectMocks
    private EndpointWriter subject;

    @Test
    public void testCreate_whenMissingRequiredRoutes_thenDefaultsAreAdded() {
        var initial = Endpoint.builder()
                .configuration(EndpointConfiguration.builder()
                        .routes(Collections.emptySet())
                        .build())
                .build();
        doAnswer(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0)))
                .when(endpointRepository).save(any(Endpoint.class));

        var result = subject.create(initial);

        StepVerifier.create(result)
                .assertNext(endpoint -> {
                    assertTrue(endpoint.hasRoute(Route.Type.CONNECT));
                    assertTrue(endpoint.hasRoute(Route.Type.DEFAULT));
                    assertTrue(endpoint.hasRoute(Route.Type.DISCONNECT));
                }).verifyComplete();
    }

    @Test
    public void testCreate_whenRequiredRoutesArePresent_thenAreNotUpdated() {
        var initial = Fixtures.defaultEndpoint();
        doAnswer(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0)))
                .when(endpointRepository).save(any(Endpoint.class));

        var result = subject.create(initial);

        StepVerifier.create(result)
                .assertNext(endpoint -> {
                    assertEquals(initial.getConnectRoute(), endpoint.getConnectRoute());
                    assertEquals(initial.getDisconnectRoute(), endpoint.getDisconnectRoute());
                    assertEquals(initial.getDefaultRoute(), endpoint.getDefaultRoute());
                }).verifyComplete();
    }

    @Test
    public void testUpdate() {
        var current = Fixtures.defaultEndpoint().withId("id");
        var updated = current.withConfiguration(
                current.getConfiguration()
                        .withAuthentication(new Authentication.Bearer("aaaa"))
                ).withPath("new");
        doReturn(Mono.just(current)).when(endpointsProvider).getById("id");
        doAnswer(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0)))
                .when(endpointRepository).save(any(Endpoint.class));

        var result = subject.update("id", updated);

        StepVerifier.create(result)
                .expectNext(updated)
                .verifyComplete();
    }
}