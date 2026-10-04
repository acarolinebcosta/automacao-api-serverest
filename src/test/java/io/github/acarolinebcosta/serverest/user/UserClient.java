package io.github.acarolinebcosta.serverest.user;

import io.github.acarolinebcosta.serverest.api.ApiConfig;
import io.restassured.response.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class UserClient {

    @NonNull
    private final ApiConfig config;

    public Response create(UserRequest user) {
        return config.request()
                .body(user)
                .when()
                .post("/usuarios");
    }

    public Response createRaw(Map<String, Object> rawPayload) {
        return config.request()
                .body(rawPayload)
                .when()
                .post("/usuarios");
    }

    public Response findById(String id) {
        return config.request()
                .pathParam("id", id)
                .when()
                .get("/usuarios/{id}");
    }

    public Response delete(String id) {
        return config.request()
                .pathParam("id", id)
                .when()
                .delete("/usuarios/{id}");
    }
}