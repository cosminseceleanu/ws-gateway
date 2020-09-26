package com.cosmin.wsgateway.application.gateway.pipeline.operators;

import com.cosmin.wsgateway.application.gateway.PayloadTransformer;
import com.cosmin.wsgateway.application.gateway.connection.Connection;
import com.cosmin.wsgateway.application.gateway.connection.Message;
import com.cosmin.wsgateway.domain.Event;
import com.cosmin.wsgateway.domain.events.Connected;
import com.cosmin.wsgateway.domain.events.Disconnected;
import com.cosmin.wsgateway.domain.events.UserMessage;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class InboundFlowOperator implements Function<Flux<Message>, Flux<Event>> {

    private final Function<Flux<Event>, Flux<Event>> sendToBackendsOperator;
    private final PayloadTransformer transformer;
    private final Connection.Context context;

    @Override
    public Flux<Event> apply(Flux<Message> flux) {
        return flux
                .transform(toInboundEventOperator())
                .transform(connectionLifecycleEventsOperator())
                .publishOn(context.getScheduler())
                .transform(sendToBackendsOperator)
                .transform(Operators.logEventOperator());
    }

    private Function<Flux<Message>, Flux<Event>> toInboundEventOperator() {
        return flux -> flux
                .map(Message::getPayload)
                .map(msg -> new UserMessage(context.getConnectionId(), transformer.toPayload(msg, Object.class), msg));
    }

    private Function<Flux<Event>, Flux<Event>> connectionLifecycleEventsOperator() {
        return flux -> flux
                .startWith(new Connected(context.getConnectionId()))
                .concatWithValues(new Disconnected(context.getConnectionId()));
    }
}
