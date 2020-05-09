package com.cosmin.wsgateway.api.mappers;

import static org.junit.jupiter.api.Assertions.*;

import com.cosmin.wsgateway.api.representation.AuthenticationRepresentation;
import com.cosmin.wsgateway.domain.Authentication;
import org.junit.jupiter.api.Test;

class AuthenticationMapperTest {
    private AuthenticationMapper subject = new AuthenticationMapper();

    @Test
    public void testToModel_representationModeIsNone_shouldReturnCorrectModel() {
        var representation = new AuthenticationRepresentation.None();
        var result = subject.toModel(representation);
        var expected = new Authentication.None();

        assertEquals(expected, result);
    }

    @Test
    public void testToModel_representationModeIsBasic_shouldReturnCorrectModel() {
        var representation = new AuthenticationRepresentation.Basic("user", "pass");
        var result = subject.toModel(representation);
        var expected = new Authentication.Basic("user", "pass");

        assertEquals(expected, result);
    }

    @Test
    public void testToModel_representationModeIsBearer_shouldReturnCorrectModel() {
        var representation = new AuthenticationRepresentation.Bearer("serverUrl");
        var result = subject.toModel(representation);
        var expected = new Authentication.Bearer("serverUrl");

        assertEquals(expected, result);
    }

    @Test
    public void testToRepresentation_basicAuthModel_shouldReturnRepresentationWthCorrectMode() {
        var model = new Authentication.Basic("user", "pass");
        var expected = new AuthenticationRepresentation.Basic("user", "pass");
        var result = subject.toRepresentation(model);

        assertEquals(expected, result);
    }

    @Test
    public void testToRepresentation_noneAuthModel_shouldReturnRepresentationWthCorrectMode() {
        var model = new Authentication.None();
        var expected = new AuthenticationRepresentation.None();
        var result = subject.toRepresentation(model);

        assertEquals(expected, result);
    }

    @Test
    public void testToRepresentation_bearerAuthModel_shouldReturnRepresentationWthCorrectMode() {
        var model = new Authentication.Bearer("serverUrl");
        var expected = new AuthenticationRepresentation.Bearer("serverUrl");
        var result = subject.toRepresentation(model);

        assertEquals(expected, result);
    }
}