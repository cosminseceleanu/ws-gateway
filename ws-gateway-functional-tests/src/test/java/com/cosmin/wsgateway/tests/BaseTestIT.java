package com.cosmin.wsgateway.tests;

import com.cosmin.wsgateway.api.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public abstract class BaseTestIT {
    @LocalServerPort
    protected int port;

    @Autowired
    protected WebTestClient webClient;
}
