package com.cosmin.wsgateway.api.config;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class EndpointVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Vertx vertx = Vertx.factory.vertx();
    Router router = Router.router(vertx);
    HttpServer httpServer = vertx.createHttpServer();
    httpServer.websocketHandler((ctx) -> {
      ctx.writeTextMessage("ping");

      ctx.textMessageHandler((msg) -> {
        System.out.println("Server " + msg);

        if ((new Random()).nextInt(100) == 0) {
          ctx.close();
        } else {
          ctx.writeTextMessage("ping");
        }
      });
    });

    httpServer.requestHandler(router::accept).listen(config().getInteger("http.port", 8081), result -> {
      if (result.succeeded()) {
        startFuture.complete();
      } else {
        startFuture.fail("it failed");
      }
    });
  }
}
