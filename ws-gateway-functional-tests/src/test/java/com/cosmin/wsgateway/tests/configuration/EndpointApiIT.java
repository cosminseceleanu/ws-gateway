package com.cosmin.wsgateway.tests.configuration;

import com.cosmin.wsgateway.infrastructure.representation.AuthenticationRepresentation;
import com.cosmin.wsgateway.infrastructure.representation.EndpointRepresentation;
import com.cosmin.wsgateway.infrastructure.representation.ErrorRepresentation;
import com.cosmin.wsgateway.tests.BaseTestIT;
import com.cosmin.wsgateway.tests.Tags;
import com.cosmin.wsgateway.tests.common.EndpointFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tags.API
public class EndpointApiIT extends BaseTestIT {

    public static final String ERROR_TYPE_ENDPOINT_NOT_FOUND = "EndpointNotFound";
    public static final String ERROR_TYPE_CONSTRAINT_VIOLATION = "ConstraintViolation";

    @Test
    public void createSuccessfulEndpoint() {
        var initial = EndpointFixtures.defaultRepresentation();

        var created = endpointsClient.create(initial)
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody(EndpointRepresentation.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created.getId());
        assertEquals(initial.getAuthentication(), created.getAuthentication());
        assertEquals(initial.getPath(), created.getPath());
        assertEquals(initial.getRoutes(), created.getRoutes());
    }

    @Test
    public void createEndpointWithInvalidPayloadReturn400() {
        Given("invalid endpoint body");
        var initial = EndpointFixtures.defaultRepresentation();
        initial.setPath(null);
        initial.setAuthentication(null);

        When("is created");
        var errorRepresentation = endpointsClient.create(initial)
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(ErrorRepresentation.class)
                .returnResult()
                .getResponseBody();

        Then("constraint violation error is returned");
        assertNotNull(errorRepresentation);
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorRepresentation.getStatus());
        assertEquals(ERROR_TYPE_CONSTRAINT_VIOLATION, errorRepresentation.getErrorType());
    }

    @Test
    public void deleteANonExistingEndpointReturns404() {
        When("delete a non existing endpoint");
        var errorRepresentation = endpointsClient.delete("non-existing-id")
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(ErrorRepresentation.class)
                .returnResult()
                .getResponseBody();

        Then("404 is returned");
        assertNotNull(errorRepresentation);
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), errorRepresentation.getStatus());
        assertEquals(ERROR_TYPE_ENDPOINT_NOT_FOUND, errorRepresentation.getErrorType());
    }

    @Test
    public void deleteEndpointById() {
        Given("an endpoint");
        var initial = EndpointFixtures.defaultRepresentation();
        var created = endpointsClient.createAndAssert(initial);

        When("is deleted");
        endpointsClient.deleteAndAssert(created.getId());

        Then("when get by it return 404");
        var errorRepresentation = endpointsClient.getById(created.getId())
                .expectStatus().isNotFound()
                .expectBody(ErrorRepresentation.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(errorRepresentation);
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), errorRepresentation.getStatus());
    }

    @Test
    public void getAllEndpoints() {
        var result = endpointsClient.getAllAndAssert();
        assertNotNull(result);
    }

    @Test
    public void getByNonExistingIdShouldReturn404() {
        var response = endpointsClient.getById("some-none-existing-id");

        var errorRepresentation = response
                .expectStatus().isNotFound()
                .expectBody(ErrorRepresentation.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(errorRepresentation);
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), errorRepresentation.getStatus());
        assertEquals(ERROR_TYPE_ENDPOINT_NOT_FOUND, errorRepresentation.getErrorType());
    }

    @Test
    public void getById() {
        var initial = EndpointFixtures.defaultRepresentation();
        var created = endpointsClient.createAndAssert(initial);

        var representation = endpointsClient.getById(created.getId())
                .expectStatus().isOk()
                .expectBody(EndpointRepresentation.class)
                .returnResult()
                .getResponseBody();

        assertEquals(created, representation);
    }

    @Test
    public void updateEndpointWithValidBody() {
        Given("an endpoint");
        var initial = EndpointFixtures.defaultRepresentation();
        var created = endpointsClient.createAndAssert(initial);

        When("is updated");
        created.setPath("my-new-endpoint");
        created.setAuthentication(new AuthenticationRepresentation.Basic("username", "password"));
        var updated = endpointsClient.updateAndAssert(created.getId(), created);

        Then("endpoint is successfully updated");
        assertNotNull(updated);
        assertEquals(created.getAuthentication(), updated.getAuthentication());
        assertEquals(created.getPath(), updated.getPath());
    }

    @Test
    public void updateEndpointMissingEndpoint() {
        When("is a missing endpoint is updated");
        var errorRepresentation = endpointsClient.update("noc-existin-id", EndpointFixtures.defaultRepresentation())
                .expectStatus().isNotFound()
                .expectBody(ErrorRepresentation.class)
                .returnResult()
                .getResponseBody();

        Then("404 is returned");
        assertNotNull(errorRepresentation);
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), errorRepresentation.getStatus());
    }

    @Test
    public void updateEndpointWithInvalidBody() {
        Given("an endpoint");
        var initial = EndpointFixtures.defaultRepresentation();
        var created = endpointsClient.createAndAssert(initial);

        When("is updated with invalid body");
        created.setPath(null);
        var errorRepresentation = endpointsClient.update(created.getId(), created)
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(ErrorRepresentation.class)
                .returnResult()
                .getResponseBody();

        Then("endpoint is not updated and returns 400");
        assertNotNull(errorRepresentation);
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorRepresentation.getStatus());
    }

}
