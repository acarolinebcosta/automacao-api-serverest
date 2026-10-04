package io.github.acarolinebcosta.serverest.user;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class UserSteps {

    private final UserService userService;

    private Response creationResponse;

    @Dado("que possuo os dados de um novo usuário administrador")
    public void administratorUserDataExists() {
        userService.prepareAdmin();
    }

    @Quando("realizo o cadastro do usuário")
    public void createAdministratorUser() {
        creationResponse = userService.createPreparedAdmin();
    }

    @Entao("o usuário deve ser criado com sucesso")
    public void administratorUserShouldBeCreated() {
        assertCreationResponseAvailable();
        userService.validateCreation(creationResponse);
    }

    @Entao("os dados do usuário devem estar persistidos")
    public void administratorUserShouldBePersisted() {
        userService.validatePersistence();
    }

    @Dado("que existe um usuário administrador autenticado")
    public void authenticatedAdminExists() {
        userService.createAuthenticatedAdmin();
    }

    private void assertCreationResponseAvailable() {
        if (creationResponse == null) {
            throw new IllegalStateException(
                    "User creation response is not available"
            );
        }
    }
}
