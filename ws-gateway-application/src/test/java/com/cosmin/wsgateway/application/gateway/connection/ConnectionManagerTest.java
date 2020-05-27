package com.cosmin.wsgateway.application.gateway.connection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.cosmin.wsgateway.application.Fixtures;
import com.cosmin.wsgateway.application.configuration.services.EndpointsProvider;
import com.cosmin.wsgateway.application.gateway.connection.filters.ConnectionFilter;
import com.cosmin.wsgateway.domain.exceptions.EndpointNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ConnectionManagerTest {

    @Mock
    private EndpointsProvider endpointsProvider;

    @Mock
    private ConnectionFactory connectionFactory;

    private List<ConnectionFilter> filters = Collections.emptyList();

    private ConnectionManager subject;

    @BeforeEach
    void setUp() {
        subject = new ConnectionManager(endpointsProvider, connectionFactory, filters);
    }

    @Test
    public void testConnect_whenPathIsNotSupported_thenConnectionIsClosedWithException() {
        doReturn(Mono.error(new EndpointNotFoundException("not found"))).when(endpointsProvider).getFirstMatch("/path");
        var result = subject.connect(ConnectionRequest.builder().path("/path").build());

        StepVerifier.create(result)
                .expectError(EndpointNotFoundException.class)
                .verify();
        verify(connectionFactory, never()).create(any(), any());
    }

    @Test
    public void testConnect_whenSuccessful_thenFiltersChainIsExecuted() {
        var endpoint = Fixtures.defaultEndpoint();
        doReturn(Mono.just(endpoint)).when(endpointsProvider).getFirstMatch(endpoint.getPath());

        var initialRequest = ConnectionRequest.builder().path(endpoint.getPath()).build();
        var filter1 = mock(ConnectionFilter.class);
        var request1 = ConnectionRequest.builder().headers(Map.of("k1", "v1")).build();
        doReturn(Mono.just(request1)).when(filter1).filter(endpoint, initialRequest);

        var filter2 = mock(ConnectionFilter.class);
        var request2 = ConnectionRequest.builder().headers(Map.of("k1", "v1", "k2", "v2")).build();
        doReturn(Mono.just(request2)).when(filter2).filter(endpoint, request1);

        var expectedConnection = new Connection(null, null, null, null, null);
        doReturn(expectedConnection).when(connectionFactory).create(endpoint, request2);

        subject = new ConnectionManager(endpointsProvider, connectionFactory, List.of(filter1, filter2));
        var result = subject.connect(ConnectionRequest.builder().path(endpoint.getPath()).build());

        StepVerifier.create(result)
                .expectNext(expectedConnection)
                .verifyComplete();
    }

    @Test
    public void testConnect_whenFilterThrowsException_thenFiltersChainIsStopped() {
        var endpoint = Fixtures.defaultEndpoint();
        doReturn(Mono.just(endpoint)).when(endpointsProvider).getFirstMatch(endpoint.getPath());

        var initialRequest = ConnectionRequest.builder().path(endpoint.getPath()).build();
        var filter1 = mock(ConnectionFilter.class);
        doReturn(Mono.just(Mono.error(new RuntimeException("test")))).when(filter1).filter(endpoint, initialRequest);

        var filter2 = mock(ConnectionFilter.class);

        subject = new ConnectionManager(endpointsProvider, connectionFactory, List.of(filter1, filter2));
        var result = subject.connect(ConnectionRequest.builder().path(endpoint.getPath()).build());

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
        verify(connectionFactory, never()).create(any(), any());
        verify(filter2, never()).filter(any(), any());
    }
}