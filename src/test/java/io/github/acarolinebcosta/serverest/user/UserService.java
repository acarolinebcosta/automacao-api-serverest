package io.github.acarolinebcosta.serverest.user;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.api.CreateResponse;
import io.github.acarolinebcosta.serverest.auth.LoginClient;
import io.github.acarolinebcosta.serverest.auth.LoginRequest;
import io.github.acarolinebcosta.serverest.auth.LoginResponse;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.validation.ContractAssertions;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public final class UserService {

    private final ScenarioContext context;
    private final UserClient userClient;
    private final LoginClient loginClient;
    private final ContractAssertions contractAssertions;

    @Step("Preparar dados do usuário administrador")
    public void prepareAdmin() {
        assertThat(context.getUser())
                .as("O cenário não deve conter um usuário previamente")
                .isNull();

        context.setUser(UserData.validAdmin());
    }

    @Step("Criar usuário administrador")
    public Response createPreparedAdmin() {
        UserRequest user = requireUser();

        Response response = userClient.create(user);

        if (response.statusCode() == 201) {
            CreateResponse createdUser = response.as(CreateResponse.class);
            context.setUserId(createdUser.id());
        }

        return response;
    }

    @Step("Validar criação do usuário administrador")
    public void validateCreation(Response response) {
        ResponseAssertions.assertStatus(
                response,
                201,
                "Criar usuário administrador"
        );

        assertThat(context.getUserId())
                .as("ID do usuário criado")
                .isNotBlank();

        ResponseAssertions.assertMessage(
                response,
                201,
                ApiMessages.CREATE_SUCCESS,
                "Criar usuário administrador"
        );
    }

    @Step("Validar persistência do usuário administrador")
    public void validatePersistence() {
        String userId = requireUserId();
        UserRequest expectedUser = requireUser();

        Response response = userClient.findById(userId);

        ResponseAssertions.assertStatus(
                response,
                200,
                "Buscar usuário criado"
        );

        UserResponse persistedUser = response.as(UserResponse.class);

        assertThat(persistedUser.id())
                .as("ID do usuário persistido")
                .isEqualTo(userId);

        assertThat(persistedUser.name())
                .as("Nome do usuário persistido")
                .isEqualTo(expectedUser.name());

        assertThat(persistedUser.email())
                .withFailMessage(
                        "O e-mail do usuário persistido difere da requisição; valores omitidos"
                )
                .isEqualTo(expectedUser.email());

        assertThat(persistedUser.administrator())
                .as("Permissão de administrador persistida")
                .isEqualTo(expectedUser.administrator());
    }

    @Step("CT01 - Criar usuário administrador e confirmar persistência")
    public void createAdmin() {
        prepareAdmin();

        Response response = createPreparedAdmin();

        validateCreation(response);
        validatePersistence();
    }

    @Step("CT02 - Autenticar usuário criado e obter token")
    public void authenticate() {
        UserRequest user = requireUser();

        assertThat(context.getToken())
                .withFailMessage(
                        "O cenário já contém um token de autenticação; valor omitido"
                )
                .isNull();

        Response response = loginClient.login(
                new LoginRequest(
                        user.email(),
                        user.password()
                )
        );

        ResponseAssertions.assertStatus(
                response,
                200,
                "Autenticar usuário criado"
        );

        contractAssertions.validateLogin(response);

        LoginResponse loginResponse = response.as(LoginResponse.class);

        ResponseAssertions.assertMessage(
                response,
                200,
                ApiMessages.LOGIN_SUCCESS,
                "Autenticar usuário criado"
        );

        context.setToken(loginResponse.authorization());

        assertThat(context.getToken())
                .withFailMessage(
                        "A autorização deve conter um token Bearer não vazio; valor omitido"
                )
                .isNotBlank()
                .startsWith("Bearer ");
    }

    public void createAuthenticatedAdmin() {
        createAdmin();
        authenticate();
    }

    private UserRequest requireUser() {
        return assertThat(context.getUser())
                .as("Os dados do usuário administrador devem estar preparados")
                .isNotNull()
                .actual();
    }

    private String requireUserId() {
        return assertThat(context.getUserId())
                .as("O ID do usuário criado deve estar disponível")
                .isNotBlank()
                .actual();
    }
}