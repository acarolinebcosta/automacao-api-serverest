package io.github.acarolinebcosta.serverest.auth;

import io.github.acarolinebcosta.serverest.api.ApiConfig;
import io.restassured.response.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

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

    public Response loginRaw(Map<String, Object> rawPayload) {
        return config.request()
                .body(rawPayload)
                .when()
                .post("/login");
    }
}