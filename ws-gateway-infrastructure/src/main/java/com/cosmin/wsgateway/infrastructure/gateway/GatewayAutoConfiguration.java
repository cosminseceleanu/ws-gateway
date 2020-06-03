package com.cosmin.wsgateway.infrastructure.gateway;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.cosmin.wsgateway.infrastructure.GatewayProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class GatewayAutoConfiguration {

    @Bean
    public WebClient defaultWebClient(GatewayProperties gatewayProperties) {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient -> {
                    tcpClient = tcpClient.option(
                            ChannelOption.CONNECT_TIMEOUT_MILLIS,
                            gatewayProperties.getGatewayAuthenticationHttpClient().getConnectTimeout()
                    );
                    tcpClient = tcpClient.doOnConnected(conn -> conn
                            .addHandlerLast(new ReadTimeoutHandler(
                                    gatewayProperties.getGatewayAuthenticationHttpClient().getReadTimeout(),
                                    MILLISECONDS
                            )));
                    return tcpClient;
                });
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder().clientConnector(connector).build();
    }
}
