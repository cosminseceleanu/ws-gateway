package com.cosmin.wsgateway.api;

import com.cosmin.wsgateway.api.vertx.VerticleFactory;
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
    //@ToDo make it configurable
    private static final int DEFAULT_VERTX_POOL_SIZE = 2;

    @Autowired
    private VerticleFactory verticleFactory;

    @Autowired
    private Vertx vertx;

    public static void main(String[] args) {
        Schedulers.enableMetrics();
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void startVertx() {
        var deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(DEFAULT_VERTX_POOL_SIZE);
        vertx.deployVerticle(verticleFactory::createWS, deploymentOptions);
    }
}
