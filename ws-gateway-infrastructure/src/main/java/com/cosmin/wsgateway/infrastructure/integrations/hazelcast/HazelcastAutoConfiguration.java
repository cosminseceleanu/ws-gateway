package com.cosmin.wsgateway.infrastructure.integrations.hazelcast;

import com.cosmin.wsgateway.application.gateway.PubSub;
import com.cosmin.wsgateway.infrastructure.GatewayProperties;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Hazelcast configuration
 * Official docs: https://docs.hazelcast.org/docs/latest/manual/html-single/#getting-started
 */
@Configuration
@ConditionalOnProperty(prefix = "gateway", name = "pubsub.hazelcast", matchIfMissing = true)
public class HazelcastAutoConfiguration {
    private static final Integer PORT = 5200;
    private static final String CLUSTER_NAME = "gateway-hazelcast-cluster";

    @Bean(name = "hazelcastServer")
    public HazelcastInstance hazelcastInstance(Config config) {
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean("hazelcastPubSub")
    @Primary
    public PubSub hazelcastPubSub(HazelcastInstance hazelcastInstance) {
        return new HazelcastPubSub(hazelcastInstance);
    }

    @Bean
    @Profile("k8s")
    public Config getK8sConfig(GatewayProperties gatewayProperties) {
        Config config = getConfig();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getKubernetesConfig().setEnabled(true)
                .setProperty("namespace", gatewayProperties.getKubernetes().getNamespace())
                .setProperty("service-name", gatewayProperties.getKubernetes().getServiceName());

        return config;
    }

    @Bean
    @Profile("!k8s")
    public Config getConfig() {
        Config config = new Config();
        config.getNetworkConfig()
                .setPortAutoIncrement(false)
                .setPort(PORT);
        config.setClusterName(CLUSTER_NAME);

        return config;
    }

    @Bean
    public HazelcastHealthIndicator hazelcastHealthIndicator(HazelcastInstance hazelcastInstance) {
        return new HazelcastHealthIndicator(hazelcastInstance);
    }
}
