package com.cosmin.wsgateway.infrastructure.gateway.connectors;

import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.events.BackendErrorEvent;
import com.cosmin.wsgateway.application.gateway.connector.BackendConnector;
import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.Event;
import com.cosmin.wsgateway.domain.backends.KafkaSettings;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

@Component
@RequiredArgsConstructor
public class KafkaConnector implements BackendConnector<KafkaSettings> {
    private final ConcurrentHashMap<Backend<?>, KafkaSender<String, String>> senders = new ConcurrentHashMap<>();

    private final PayloadTransformer transformer;

    @Override
    public boolean supports(Backend.Type type) {
        return Backend.Type.KAFKA.equals(type);
    }

    @Override
    public Mono<Event> sendEvent(Event event, Backend<KafkaSettings> backend) {
        var msg = transformer.fromPayload(event.payload());
        var record = new ProducerRecord<>(backend.destination(), event.connectionId(), msg);

        return getOrCreateSender(backend)
                .createOutbound()
                .send(Mono.just(record))
                .then()
                .map(r -> event)
                .onErrorResume(e -> Mono.just(BackendErrorEvent.of(event, e)));
    }

    private KafkaSender<String, String> getOrCreateSender(Backend<KafkaSettings> backend) {
        if (senders.containsKey(backend)) {
            return senders.get(backend);
        }
        Map<String, Object> props = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, backend.settings().getBootstrapServers(),
                ProducerConfig.CLIENT_ID_CONFIG, "gateway-kafka-connector",
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        );
        SenderOptions<String, String> senderOptions = SenderOptions.create(props);

        var sender = KafkaSender.create(senderOptions);
        senders.put(backend, sender);

        return sender;
    }
}
