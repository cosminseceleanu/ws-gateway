package com.cosmin.wsgateway.domain;

public interface Event {
    String connectionId();

    Object payload();
}
