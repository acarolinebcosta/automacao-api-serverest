package io.github.acarolinebcosta.serverest.validation;

import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public final class ResponseAssertions {
    private ResponseAssertions() { }

    public static void assertStatus(Response response, int expected, String operation) {
        assertThat(response.statusCode()).as("%s: HTTP status", operation).isEqualTo(expected);
    }

    public static void assertMessage(Response response, int expectedStatus, String expectedMessage, String operation) {
        assertStatus(response, expectedStatus, operation);
        assertThat(response.jsonPath().getString("message"))
                .withFailMessage("%s: unexpected business message; see sanitized HTTP evidence", operation)
                .isEqualTo(expectedMessage);
    }
}
