package com.cosmin.wsgateway.infrastructure.gateway.connectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.events.BackendErrorEvent;
import com.cosmin.wsgateway.domain.backends.KafkaBackend;
import com.cosmin.wsgateway.domain.backends.KafkaSettings;
import com.cosmin.wsgateway.domain.events.Connected;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class KafkaConnectorTest {

    private final KafkaBackend kafkaBackend = KafkaBackend.builder()
            .topic("topic")
            .settings(KafkaSettings.builder()
                    .bootstrapServers("servers")
                    .acks(KafkaSettings.Ack.ALL)
                    .retriesNr(1)
                    .build())
            .build();
    @Mock
    private PayloadTransformer payloadTransformer;

    @Mock
    private KafkaSenderFactory senderFactory;

    @InjectMocks
    private KafkaConnector subject;

    @Mock
    private KafkaSender<String, String> kafkaSender;

    @BeforeEach
    void setUp() {
        when(payloadTransformer.fromPayload(any())).thenReturn("hello world");
        when(senderFactory.create(any())).thenReturn(kafkaSender);
    }

    @Test
    public void testSendEvent_eventIsSuccessfulSent_shouldReturnInitialEvent() {
        var event = new Connected("id");
        when(kafkaSender.send(any())).thenReturn(Flux.just(MockResult.of(null)));

        var result = subject.sendEvent(event, kafkaBackend);

        StepVerifier.create(result)
                .expectNext(event)
                .verifyComplete();
    }

    @Test
    public void testSendEvent_multipleEventsAreSentToTheSameBackend_thenOnlyOneSenderIsCreated() {
        var event = new Connected("id");
        when(kafkaSender.send(any())).thenReturn(Flux.just(MockResult.of(null)));

        subject.sendEvent(event, kafkaBackend);
        subject.sendEvent(event, kafkaBackend);
        subject.sendEvent(event, kafkaBackend);

        verify(senderFactory, times(1)).create(any());
    }

    @Test
    public void testSendEvent_eventIsNotSent_shouldAnErrorEvent() {
        var initial = new Connected("id");
        var error = new RuntimeException("error");
        when(kafkaSender.send(any())).thenReturn(Flux.just(MockResult.of(error)));

        var result = subject.sendEvent(initial, kafkaBackend);

        StepVerifier.create(result)
                .expectNext(BackendErrorEvent.of(initial, error))
                .verifyComplete();
    }

    @Test
    public void testSendEvent_givenABackend_shouldCreateSenderWithProperProperties() {
        var event = new Connected("id");
        ArgumentCaptor<SenderOptions<String, String>> senderOptionsCaptor = ArgumentCaptor.forClass(SenderOptions.class);

        when(kafkaSender.send(any())).thenReturn(Flux.just(MockResult.of(null)));

        subject.sendEvent(event, kafkaBackend);
        verify(senderFactory, times(1)).create(senderOptionsCaptor.capture());
        var options = senderOptionsCaptor.getValue();
        assertNotNull(options);
        assertEquals(kafkaBackend.settings().getAcks().getKafkaValue(), options.producerProperty(ProducerConfig.ACKS_CONFIG));
        assertEquals(kafkaBackend.settings().getRetriesNr(), options.producerProperty(ProducerConfig.RETRIES_CONFIG));
        assertEquals(kafkaBackend.settings().getBootstrapServers(), options.producerProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

    @AllArgsConstructor(staticName = "of")
    private static class MockResult implements SenderResult<Object> {
        private Exception exception;

        @Override
        public RecordMetadata recordMetadata() {
            return null;
        }

        @Override
        public Exception exception() {
            return exception;
        }

        @Override
        public Object correlationMetadata() {
            return null;
        }
    }
}