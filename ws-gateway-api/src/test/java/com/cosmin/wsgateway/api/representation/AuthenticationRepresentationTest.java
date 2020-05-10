package com.cosmin.wsgateway.api.representation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.api.utils.JsonUtils;
import org.junit.jupiter.api.Test;

class AuthenticationRepresentationTest {

    private static final String BEARER_JSON = "{\"authorizationServerUrl\":\"serverUrl\",\"mode\":\"bearer\"}";

    @Test
    public void testBearerAuth_serializeToJson_jsonIsValid() {
        var representation = new AuthenticationRepresentation.Bearer("serverUrl");

        var result = JsonUtils.toJson(representation);

        assertEquals(BEARER_JSON, result);
    }

    @Test
    public void testBearerAuth_deserializeFromJson_represensationHasFieldsPresent() {
        var result = JsonUtils.fromJson(BEARER_JSON, AuthenticationRepresentation.class);

        assertThat(result, instanceOf(AuthenticationRepresentation.Bearer.class));
        var bearerAuth = (AuthenticationRepresentation.Bearer) result;
        assertEquals("serverUrl", bearerAuth.getAuthorizationServerUrl());
        assertEquals(AuthenticationRepresentation.Mode.BEARER, bearerAuth.mode());
    }
}