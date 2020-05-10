package com.cosmin.wsgateway.infrastructure.pubsub.ignite;

import com.cosmin.wsgateway.application.gateway.PubSub;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class IgniteAutoConfiguration {

    @Bean(name = "igniteServer")
    public Ignite igniteConfiguration() {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setClientMode(false);

        return Ignition.start(igniteConfiguration);
    }

    @Bean
    public IgniteMessaging igniteMessaging(Ignite ignite) {
        return ignite.message(ignite.cluster().forServers());
    }

    @Bean
    @Primary
    public PubSub ignitePubSub(IgniteMessaging igniteMessaging) {
        return new IgnitePubSub(igniteMessaging);
    }
}
