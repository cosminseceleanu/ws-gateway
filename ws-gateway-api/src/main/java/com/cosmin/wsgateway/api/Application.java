package com.cosmin.wsgateway.api;

import com.cosmin.wsgateway.api.vertx.VerticleFactory;
import com.cosmin.wsgateway.infrastructure.GatewayProperties;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.scheduler.Schedulers;


@SpringBootApplication(scanBasePackages = "com.cosmin.wsgateway")
@EnableScheduling
public class Application {

    @Autowired
    private VerticleFactory verticleFactory;

    @Autowired
    private Vertx vertx;

    @Autowired
    private GatewayProperties gatewayProperties;

    public static void main(String[] args) {
        Schedulers.enableMetrics();
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void startVertx() {
        var deplomentOptions = new DeploymentOptions();
        deplomentOptions.setInstances(gatewayProperties.getVertx().getGatewayVerticleInstances());
        vertx.deployVerticle(verticleFactory::createWS, deplomentOptions);
    }
}
