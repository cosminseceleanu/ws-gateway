package com.cosmin.wsgateway.application.gateway.pipeline;

import com.cosmin.wsgateway.domain.events.Connected;
import com.cosmin.wsgateway.domain.events.Disconnected;
import com.cosmin.wsgateway.domain.events.InboundEvent;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor(staticName = "newInstance")
public class AppendConnectionLifecycleEventsStage implements Function<Flux<InboundEvent>, Flux<InboundEvent>> {
    private final String connectionId;

    @Override
    public Flux<InboundEvent> apply(Flux<InboundEvent> flux) {
        return flux.startWith(new Connected(connectionId))
                .concatWithValues(new Disconnected(connectionId));
    }
}
