package io.github.acarolinebcosta.serverest.user;

import io.github.acarolinebcosta.serverest.api.ApiConfig;
import io.restassured.response.Response;

import java.util.Objects;

public final class UserClient {

    private final ApiConfig config;

    public UserClient(ApiConfig config) {
        this.config = Objects.requireNonNull(config, "API configuration is required");
    }

    public Response create(UserRequest user) {
        return config.request().body(user).when().post("/usuarios");
    }

    public Response findById(String id) {
        return config.request().pathParam("id", id).when().get("/usuarios/{id}");
    }

    public Response delete(String id) {
        return config.request().pathParam("id", id).when().delete("/usuarios/{id}");
    }
}
