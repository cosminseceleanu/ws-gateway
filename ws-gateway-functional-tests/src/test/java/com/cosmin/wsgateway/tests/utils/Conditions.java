package com.cosmin.wsgateway.tests.utils;

import com.cosmin.wsgateway.tests.client.WebSocketClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import java.util.concurrent.Callable;

public final class Conditions {
    private Conditions() {};

    public static Callable<Integer> receivedMessages(WebSocketClient.WebSocketConnection connection) {
        return () -> connection.getReceivedMessages().size();
    }

    public static Callable<Integer> receivedRequest(WireMockServer wireMock) {
        return () -> wireMock.countRequestsMatching(RequestPattern.everything()).getCount();
    }
}
