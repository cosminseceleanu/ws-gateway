package com.cosmin.wsgateway.infrastructure.gateway.connectors;

import org.springframework.stereotype.Component;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

@Component
public class KafkaSenderFactory {
    public KafkaSender<String, String> create(SenderOptions<String, String> senderOptions) {
        return KafkaSender.create(senderOptions);
    }
}
