package com.cosmin.wsgateway.tests.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.ConsumerFactory;

public final class KafkaUtils {
    private KafkaUtils() {}

    public static List<String> getRecords(String topic, EmbeddedKafkaBroker broker) {
        Map<String, Object> configs = KafkaTestUtils.consumerProps("consumer-" + topic, "false", broker);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        ReceiverOptions<String, String> receiverOptions = ReceiverOptions.create(configs);

        Consumer<String, String> consumer = ConsumerFactory.INSTANCE.createConsumer(receiverOptions);
        consumer.subscribe(Collections.singleton(topic));
        var records = KafkaTestUtils.getRecords(consumer, 5000);
        consumer.close();

        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(records.iterator(), Spliterator.ORDERED),
                false)
                .map(ConsumerRecord::value)
                .collect(Collectors.toList());
    }
}
