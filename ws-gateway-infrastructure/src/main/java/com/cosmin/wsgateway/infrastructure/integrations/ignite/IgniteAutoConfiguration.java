package com.cosmin.wsgateway.infrastructure.integrations.ignite;

import com.cosmin.wsgateway.application.gateway.GatewayProperties;
import com.cosmin.wsgateway.application.gateway.PubSub;
import io.micrometer.core.instrument.MeterRegistry;
import java.nio.file.Paths;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.managers.discovery.IgniteDiscoverySpi;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Ignite ports:
 * 11211 # REST port number.
 * 47100 # communication SPI port number.
 * 47500 # discovery SPI port number.
 * 49112 # JMX port number.
 * 10800 # SQL port number.
 * 10900 # Thin clients port number.
 */
@Configuration
public class IgniteAutoConfiguration {

    private static final int IGNITE_LOCAL_PORT = 47100;
    private static final int IGNITE_DISCOVERY_PORT = 47500;
    private static final int SYSTEM_THREAD_POOL_SIZE = 4;
    private static final int PUBLIC_THREAD_POOL_SIZE = 8;
    private static final int REBALANCE_THREAD_POOL_SIZE = 2;

    @Bean(name = "igniteServer")
    public Ignite igniteConfiguration(IgniteDiscoverySpi discoverySpi) {
        var communicationSpi = new TcpCommunicationSpi();
        communicationSpi.setLocalPort(IGNITE_LOCAL_PORT);
        communicationSpi.setLocalPortRange(0);

        var workingDir = Paths.get("target")
                .toAbsolutePath()
                .toString();
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setClientMode(false);
        igniteConfiguration.setWorkDirectory(workingDir);
        igniteConfiguration.setCommunicationSpi(communicationSpi);
        igniteConfiguration.setDiscoverySpi(discoverySpi);
        igniteConfiguration.setPublicThreadPoolSize(PUBLIC_THREAD_POOL_SIZE);
        igniteConfiguration.setRebalanceThreadPoolSize(REBALANCE_THREAD_POOL_SIZE);
        igniteConfiguration.setSystemThreadPoolSize(SYSTEM_THREAD_POOL_SIZE);

        return Ignition.start(igniteConfiguration);
    }

    @Bean
    @Profile("k8s")
    public IgniteDiscoverySpi k8sDiscoverySpi(GatewayProperties gatewayProperties) {
        var ipFinder = new TcpDiscoveryKubernetesIpFinder();
        ipFinder.setNamespace(gatewayProperties.getKubernetes().getNamespace());
        ipFinder.setServiceName(gatewayProperties.getKubernetes().getServiceName());

        var discoverySpi = new TcpDiscoverySpi();
        discoverySpi.setIpFinder(ipFinder);
        discoverySpi.setLocalPort(IGNITE_DISCOVERY_PORT);

        return discoverySpi;
    }

    @Bean
    @Profile("!k8s")
    public IgniteDiscoverySpi defaultDiscoverySpi() {
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        discoverySpi.setLocalPort(IGNITE_DISCOVERY_PORT);

        return discoverySpi;
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

    @Bean
    public IgniteHealthIndicator healthIndicator(Ignite ignite) {
        return new IgniteHealthIndicator(ignite);
    }

    @Bean
    public IgniteMetrics metrics(Ignite ignite, MeterRegistry meterRegistry) {
        return new IgniteMetrics(ignite, meterRegistry);
    }
}
