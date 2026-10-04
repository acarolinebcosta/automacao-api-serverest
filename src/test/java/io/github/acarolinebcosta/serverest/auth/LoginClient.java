package io.github.acarolinebcosta.serverest.auth;

import io.github.acarolinebcosta.serverest.api.ApiConfig;
import io.restassured.response.Response;

import java.util.Objects;

public final class LoginClient {

    private final ApiConfig config;

    public LoginClient(ApiConfig config) {
        this.config = Objects.requireNonNull(config, "API configuration is required");
    }

    public Response login(LoginRequest login) {
        return config.request().body(login).when().post("/login");
    }
}
