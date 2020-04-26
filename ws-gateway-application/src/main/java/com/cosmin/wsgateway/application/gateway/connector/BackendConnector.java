package com.cosmin.wsgateway.application.gateway.connector;

import com.cosmin.wsgateway.domain.Backend;
import com.cosmin.wsgateway.domain.BackendSettings;
import com.cosmin.wsgateway.domain.Event;
import lombok.Value;

import java.util.Optional;

public interface BackendConnector<T extends BackendSettings> {
    boolean supports(Backend.Type type);

    Result sendEvent(Event event, Backend<T> backend);

    @Value
    class Result {
        private final Event event;
        private final Backend<? extends BackendSettings> backend;
        private final Optional<Exception> error;

        public static Result ofSuccessful(Backend<? extends BackendSettings> backend, Event event) {
            return new Result(event, backend, Optional.empty());
        }

        public static Result ofFailure(Backend<? extends BackendSettings> backend, Event event, Exception exception) {
            return new Result(event, backend, Optional.of(exception));
        }

        public boolean isSuccessful() {
            return error.isEmpty();
        }
    }
}
