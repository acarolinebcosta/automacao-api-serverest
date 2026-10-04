package io.github.acarolinebcosta.serverest.validation;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

import static org.assertj.core.api.Assertions.assertThat;

@UtilityClass
public class ResponseAssertions {

    public void assertStatus(
            Response response,
            int expectedStatus,
            String operation
    ) {
        assertThat(response.statusCode())
                .as("%s: status HTTP", operation)
                .isEqualTo(expectedStatus);
    }

    public void assertMessage(
            Response response,
            int expectedStatus,
            String expectedMessage,
            String operation
    ) {
        assertStatus(response, expectedStatus, operation);

        assertThat(response.jsonPath().getString("message"))
                .withFailMessage(
                        "%s: mensagem de negócio inesperada; ver evidência HTTP sanitizada",
                        operation
                )
                .isEqualTo(expectedMessage);
    }
}