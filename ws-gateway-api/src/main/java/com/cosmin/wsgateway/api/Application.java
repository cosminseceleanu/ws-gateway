package com.cosmin.wsgateway.api;

import com.cosmin.wsgateway.api.vertx.VerticleFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.cosmin.wsgateway")
public class Application {
    //@ToDo make it configurable
    private static final int DEFAULT_VERTX_POOL_SIZE = 2;

    @Autowired
    private VerticleFactory verticleFactory;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void startVertx() {
        var deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(DEFAULT_VERTX_POOL_SIZE);
        var vertxOptions = new VertxOptions();
        vertxOptions.setEventLoopPoolSize(DEFAULT_VERTX_POOL_SIZE);

        Vertx.vertx(vertxOptions).deployVerticle(verticleFactory::createWS, deploymentOptions);
    }
}
