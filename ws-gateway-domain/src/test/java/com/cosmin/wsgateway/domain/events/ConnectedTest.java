package com.cosmin.wsgateway.domain.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ConnectedTest {
    @Test
    public void testGetPayload() {
        var subject = new Connected("id");

        assertEquals(Map.of("connectionId", "id"), subject.payload());
    }

}