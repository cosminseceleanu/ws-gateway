package com.cosmin.wsgateway.infrastructure.integrations.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "gateway", name = "persistence.mongo")
public class MongoAutoConfiguration {
}
