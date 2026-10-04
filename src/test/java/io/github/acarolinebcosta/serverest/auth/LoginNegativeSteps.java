package io.github.acarolinebcosta.serverest.auth;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class LoginNegativeSteps {

    private final LoginService loginService;
    private final ScenarioContext context;

    @Quando("tento autenticar com uma senha incorreta")
    public void attemptLoginWithInvalidPassword() {
        context.setLastResponse(
                loginService.loginWithInvalidPassword(context.getKnownEmail())
        );
    }

    @Quando("tento autenticar com um e-mail não cadastrado")
    public void attemptLoginWithUnknownEmail() {
        context.setLastResponse(loginService.loginWithUnknownEmail());
    }

    @Quando("tento autenticar sem o campo e-mail")
    public void attemptLoginWithoutEmail() {
        context.setLastResponse(loginService.loginWithoutField("email"));
    }

    @Quando("tento autenticar sem o campo senha")
    public void attemptLoginWithoutPassword() {
        context.setLastResponse(loginService.loginWithoutField("password"));
    }

    @Entao("a autenticação deve ser rejeitada com status {int}")
    public void authenticationShouldBeRejectedWithStatus(int expectedStatus) {
        ResponseAssertions.assertStatus(
                context.getLastResponse(),
                expectedStatus,
                "Rejeitar autenticação"
        );
    }

    @Entao("a mensagem de credenciais inválidas deve ser retornada")
    public void invalidCredentialsMessageShouldBeReturned() {
        ResponseAssertions.assertMessage(
                context.getLastResponse(),
                401,
                ApiMessages.INVALID_CREDENTIALS,
                "Credenciais inválidas"
        );
    }
}