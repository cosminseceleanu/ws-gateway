package com.cosmin.wsgateway.domain.events;


import com.cosmin.wsgateway.domain.Endpoint;
import com.cosmin.wsgateway.domain.Event;
import com.cosmin.wsgateway.domain.Route;

public interface InboundEvent extends Event {
    Route getRoute(Endpoint endpoint);
}
