package com.cosmin.wsgateway.api;

import com.cosmin.wsgateway.api.config.EndpointVerticle;

import io.vertx.core.Vertx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Application {

	@Autowired
	private EndpointVerticle endpointVerticle;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @PostConstruct
  public void deployVerticle() {
    Vertx.vertx().deployVerticle(endpointVerticle);
  }
}
