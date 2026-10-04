package io.github.acarolinebcosta.serverest.user;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.testdata.DataGenerator;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class UpdateSteps {

    private final ScenarioContext context;
    private final UserClient userClient;

    @Quando("atualizo o usuário com dados válidos")
    public void updateUserWithValidData() {
        Response response = userClient.updateRaw(
                context.getUserId(),
                payloadForCurrentUser()
        );
        context.setLastResponse(response);
    }

    @Quando("atualizo um usuário com ID inexistente e dados válidos")
    public void updateUserWithNonexistentId() {
        Response response = userClient.updateRaw(
                DataGenerator.nonexistentUserId(),
                payloadForNewUser()
        );

        if (response.statusCode() == 201) {
            String createdId = response.jsonPath().getString("_id");
            context.addAuxiliaryUser(createdId);
        }

        context.setLastResponse(response);
    }

    @Quando("atualizo o usuário sem o campo nome")
    public void updateUserWithoutName() {
        Map<String, Object> payload = payloadForCurrentUser();
        payload.remove("nome");

        context.setLastResponse(
                userClient.updateRaw(context.getUserId(), payload)
        );
    }

    @Quando("atualizo um usuário sem token")
    public void updateUserWithoutToken() {
        context.setLastResponse(
                userClient.updateRawWithoutToken(
                        DataGenerator.nonexistentUserId(),
                        payloadForNewUser()
                )
        );
    }

    @Entao("a atualização deve ser aprovada com status 200")
    public void updateShouldBeApproved() {
        ResponseAssertions.assertStatus(
                context.getLastResponse(),
                200,
                "Atualizar usuário"
        );
    }

    @Entao("a atualização deve criar um novo registro com status 201")
    public void updateShouldCreateNew() {
        ResponseAssertions.assertStatus(
                context.getLastResponse(),
                201,
                "Criar usuário via PUT"
        );
    }

    @Entao("a atualização deve ser rejeitada com status {int}")
    public void updateShouldBeRejectedWithStatus(int expectedStatus) {
        ResponseAssertions.assertStatus(
                context.getLastResponse(),
                expectedStatus,
                "Rejeitar atualização"
        );
    }

    @Entao("a mensagem de atualização com sucesso deve ser retornada")
    public void updateSuccessMessageShouldBeReturned() {
        ResponseAssertions.assertMessage(
                context.getLastResponse(),
                200,
                ApiMessages.UPDATE_SUCCESS,
                "Atualização com sucesso"
        );
    }

    @Entao("a mensagem de cadastro com sucesso deve ser retornada")
    public void createSuccessMessageShouldBeReturned() {
        ResponseAssertions.assertMessage(
                context.getLastResponse(),
                201,
                ApiMessages.CREATE_SUCCESS,
                "Cadastro via PUT"
        );
    }

    private Map<String, Object> payloadForCurrentUser() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("nome", "Nome Atualizado " + System.nanoTime());
        payload.put("email", context.getUser().email());
        payload.put("password", context.getUser().password());
        payload.put("administrador", "true");
        return payload;
    }

    private Map<String, Object> payloadForNewUser() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("nome", "Upsert " + System.nanoTime());
        payload.put("email", DataGenerator.uniqueEmail());
        payload.put("password", DataGenerator.uniquePassword());
        payload.put("administrador", "true");
        return payload;
    }
}