package io.github.acarolinebcosta.serverest.user;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class UserNegativeSteps {

    private final UserService userService;
    private final ScenarioContext context;

    @Dado("que existe um usuário cadastrado com e-mail conhecido")
    public void userWithKnownEmailExists() {
        userService.createAdmin();
        context.setKnownEmail(context.getUser().email());
    }

    @Quando("tento cadastrar um novo usuário com o mesmo e-mail")
    public void attemptCreateUserWithDuplicateEmail() {
        context.setLastResponse(
                userService.attemptCreateWithDuplicateEmail(context.getKnownEmail())
        );
    }

    @Quando("tento cadastrar um usuário sem o campo nome")
    public void attemptCreateUserWithoutName() {
        context.setLastResponse(userService.attemptCreateWithoutField("nome"));
    }

    @Quando("tento cadastrar um usuário sem o campo e-mail")
    public void attemptCreateUserWithoutEmail() {
        context.setLastResponse(userService.attemptCreateWithoutField("email"));
    }

    @Quando("tento cadastrar um usuário sem o campo senha")
    public void attemptCreateUserWithoutPassword() {
        context.setLastResponse(userService.attemptCreateWithoutField("password"));
    }

    @Quando("tento cadastrar um usuário sem o campo administrador")
    public void attemptCreateUserWithoutAdministrator() {
        context.setLastResponse(userService.attemptCreateWithoutField("administrador"));
    }

    @Quando("busco um usuário com ID inexistente")
    public void findNonexistentUser() {
        context.setLastResponse(userService.findNonexistentUser());
    }

    @Entao("a criação do usuário deve ser rejeitada com status 400")
    public void userCreationShouldBeRejected() {
        Response response = context.getLastResponse();

        ResponseAssertions.assertStatus(response, 400, "Rejeitar criação de usuário");
    }

    @Entao("a mensagem de e-mail duplicado deve ser retornada")
    public void duplicateEmailMessageShouldBeReturned() {
        Response response = context.getLastResponse();

        ResponseAssertions.assertMessage(
                response,
                400,
                ApiMessages.DUPLICATE_EMAIL,
                "Validar e-mail duplicado"
        );
    }

    @Entao("a mensagem de usuário não encontrado deve ser retornada")
    public void userNotFoundMessageShouldBeReturned() {
        Response response = context.getLastResponse();

        ResponseAssertions.assertMessage(
                response,
                400,
                ApiMessages.USER_NOT_FOUND,
                "Validar usuário não encontrado"
        );
    }
}