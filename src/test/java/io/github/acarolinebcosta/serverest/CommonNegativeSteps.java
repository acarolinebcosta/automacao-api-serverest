package io.github.acarolinebcosta.serverest;

import io.cucumber.java.pt.Entao;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CommonNegativeSteps {

    private final ScenarioContext context;

    @Entao("a operação deve ser rejeitada com status {int}")
    public void operationShouldBeRejectedWithStatus(int expectedStatus) {
        Response response = context.getLastResponse();

        ResponseAssertions.assertStatus(
                response,
                expectedStatus,
                "Rejeitar operação"
        );
    }

    @Entao("a mensagem {string} deve ser retornada")
    public void businessMessageShouldBeReturned(String expectedMessage) {
        Response response = context.getLastResponse();

        ResponseAssertions.assertMessage(
                response,
                400,
                expectedMessage,
                "Validar mensagem de negócio"
        );
    }

    @Entao("a mensagem {string} deve ser retornada para o campo {string}")
    public void validationMessageShouldBeReturned(String expectedMessage, String fieldName) {
        Response response = context.getLastResponse();

        ResponseAssertions.assertValidationError(
                response,
                400,
                fieldName,
                expectedMessage,
                "Validar erro de validação"
        );
    }
}