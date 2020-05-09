package com.cosmin.wsgateway.api.controllers;

import com.cosmin.wsgateway.api.utils.JsonUtils;
import com.cosmin.wsgateway.application.gateway.PubSub;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class ConnectionsController {

    private final PubSub pubSub;

    @PostMapping(value = "/connections/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> create(
            @PathVariable String id,
            @NotNull @RequestBody Object payload
    ) {
        return Mono.just(payload)
                .map(JsonUtils::toJson)
                .flatMap(msg -> pubSub.publish(Connection.getOutboundTopic(id), msg));
    }
}
