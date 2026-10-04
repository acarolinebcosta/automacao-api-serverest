package io.github.acarolinebcosta.serverest.auth;

import io.github.acarolinebcosta.serverest.api.ApiConfig;
import io.restassured.response.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * HTTP client for the ServeRest authentication resource.
 *
 * Encapsulates calls to the {@code /login} endpoint without orchestrating
 * business flows or performing response validations.
 */
@RequiredArgsConstructor
public final class LoginClient {

    @NonNull
    private final ApiConfig config;

    public Response login(LoginRequest login) {
        return config.request()
                .body(login)
                .when()
                .post("/login");
    }
}