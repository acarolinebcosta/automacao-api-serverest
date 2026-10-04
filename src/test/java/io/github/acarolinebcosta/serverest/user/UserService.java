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

    @Step("Prepare administrator user data")
    public void prepareAdmin() {
        assertThat(context.getUser())
                .as("Scenario must not already contain a user")
                .isNull();

        context.setUser(UserData.validAdmin());
    }

    @Step("Create administrator user")
    public Response createPreparedAdmin() {
        assertThat(context.getUser())
                .as("Administrator user data must be prepared before creation")
                .isNotNull();

        Response response = userClient.create(context.getUser());

        if (response.statusCode() == 201) {
            CreateResponse createdUser = response.as(CreateResponse.class);
            context.setUserId(createdUser.id());
        }

        return response;
    }

    @Step("Validate administrator user creation")
    public void validateCreation(Response response) {
        ResponseAssertions.assertStatus(
                response,
                201,
                "Create administrator user"
        );

        assertThat(context.getUserId())
                .as("Created user ID")
                .isNotBlank();

        ResponseAssertions.assertMessage(
                response,
                201,
                ApiMessages.CREATE_SUCCESS,
                "Create administrator user"
        );
    }

    @Step("Validate persisted administrator user")
    public void validatePersistence() {
        assertThat(context.getUserId())
                .as("Created user ID must be available before persistence validation")
                .isNotBlank();

        Response response = userClient.findById(context.getUserId());

        ResponseAssertions.assertStatus(
                response,
                200,
                "Find created user"
        );

        UserResponse persistedUser = response.as(UserResponse.class);
        UserRequest expectedUser = context.getUser();

        assertThat(persistedUser.id())
                .as("Persisted user ID")
                .isEqualTo(context.getUserId());

        assertThat(persistedUser.name())
                .as("Persisted user name")
                .isEqualTo(expectedUser.name());

        assertThat(persistedUser.email())
                .withFailMessage(
                        "Persisted user email differs from the request; values redacted"
                )
                .isEqualTo(expectedUser.email());

        assertThat(persistedUser.administrator())
                .as("Persisted administrator permission")
                .isEqualTo(expectedUser.administrator());
    }

    @Step("CT01 - Create administrator user and confirm persistence")
    public void createAdmin() {
        prepareAdmin();

        Response response = createPreparedAdmin();

        validateCreation(response);
        validatePersistence();
    }

    @Step("CT02 - Authenticate created user and obtain token")
    public void authenticate() {
        assertThat(context.getUser())
                .as("User must be created before authentication")
                .isNotNull();

        assertThat(context.getToken())
                .withFailMessage(
                        "Scenario already contains an authentication token; value redacted"
                )
                .isNull();

        UserRequest user = context.getUser();

        Response response = loginClient.login(
                new LoginRequest(user.email(), user.password())
        );

        ResponseAssertions.assertStatus(
                response,
                200,
                "Authenticate created user"
        );

        contractAssertions.validateLogin(response);

        context.setToken(
                response.as(LoginResponse.class).authorization()
        );

        ResponseAssertions.assertMessage(
                response,
                200,
                ApiMessages.LOGIN_SUCCESS,
                "Authenticate created user"
        );

        assertThat(context.getToken())
                .withFailMessage(
                        "Authorization must be a nonblank Bearer token; value redacted"
                )
                .isNotBlank()
                .startsWith("Bearer ");
    }

    public void createAuthenticatedAdmin() {
        createAdmin();
        authenticate();
    }
}
