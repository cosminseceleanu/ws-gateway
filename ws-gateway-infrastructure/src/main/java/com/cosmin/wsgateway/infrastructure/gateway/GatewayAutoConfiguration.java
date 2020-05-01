package com.cosmin.wsgateway.infrastructure.gateway;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

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

    public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 2000;
    public static final int DEFAULT_READ_TIMEOUT_MILLIS = 10000;

    @Bean
    public WebClient defaultWebClient() {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient -> {
                    tcpClient = tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_CONNECT_TIMEOUT_MILLIS);
                    tcpClient = tcpClient.doOnConnected(conn -> conn
                            .addHandlerLast(new ReadTimeoutHandler(DEFAULT_READ_TIMEOUT_MILLIS, MILLISECONDS)));
                    return tcpClient;
                });
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder().clientConnector(connector).build();
    }
}
