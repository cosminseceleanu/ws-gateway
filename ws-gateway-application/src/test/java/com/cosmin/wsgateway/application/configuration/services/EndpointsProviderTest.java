package com.cosmin.wsgateway.application.configuration.services;

import static org.mockito.Mockito.doReturn;

import com.cosmin.wsgateway.application.configuration.repositories.EndpointRepository;
import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.exceptions.EndpointNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class EndpointsProviderTest {
    @Mock
    private EndpointRepository endpointRepository;

    @InjectMocks
    private EndpointsProvider subject;

    @Test
    public void testGetById() {
        var expected = Endpoint.builder().id("id").build();
        doReturn(Mono.just(Optional.of(expected))).when(endpointRepository).getById("id");

        var result = subject.getById("id");
        StepVerifier.create(result)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    public void testGetById_whenNotFound_exceptionIsThrown() {
        doReturn(Mono.just(Optional.empty())).when(endpointRepository).getById("id");

        var result = subject.getById("id");
        StepVerifier.create(result)
                .expectError(EndpointNotFoundException.class)
                .verify();
    }

    @Test
    public void testGetFirstMatch() {
        var endpoint1 = Endpoint.builder().path("/test").build();
        var endpoint2 = Endpoint.builder().path("/test").build();
        doReturn(Flux.just(endpoint1, endpoint2)).when(endpointRepository).getAll();

        var result = subject.getFirstMatch("/test");
        StepVerifier.create(result)
                .expectNext(endpoint1)
                .verifyComplete();
    }

    @Test
    public void testGetFirstMatch_whenNoEndpointMatches_thenThrowException() {
        var endpoint1 = Endpoint.builder().path("/test").build();
        var endpoint2 = Endpoint.builder().path("/test/2").build();
        doReturn(Flux.just(endpoint1, endpoint2)).when(endpointRepository).getAll();

        var result = subject.getFirstMatch("/ws");
        StepVerifier.create(result)
                .expectError(EndpointNotFoundException.class)
                .verify();
    }

    @Test
    public void testGetAll() {
        var endpoint1 = Endpoint.builder().path("/test").build();
        var endpoint2 = Endpoint.builder().path("/test/2").build();
        doReturn(Flux.just(endpoint1, endpoint2)).when(endpointRepository).getAll();

        var result = subject.getAll();
        StepVerifier.create(result)
                .expectNext(endpoint1)
                .expectNext(endpoint2)
                .verifyComplete();
    }
}