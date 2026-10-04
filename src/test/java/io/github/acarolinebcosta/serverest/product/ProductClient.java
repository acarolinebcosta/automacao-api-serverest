package io.github.acarolinebcosta.serverest.product;

import io.github.acarolinebcosta.serverest.api.ApiConfig;
import io.restassured.response.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public final class ProductClient {

    @NonNull
    private final ApiConfig config;

    public Response create(String token, ProductRequest product) {
        return config.authenticated(token)
                .body(product)
                .when()
                .post("/produtos");
    }

    public Response createRaw(String token, Map<String, Object> rawPayload) {
        return config.authenticated(token)
                .body(rawPayload)
                .when()
                .post("/produtos");
    }

    public Response findById(String id) {
        return config.request()
                .pathParam("id", id)
                .when()
                .get("/produtos/{id}");
    }

    public Response delete(String token, String id) {
        return config.authenticated(token)
                .pathParam("id", id)
                .when()
                .delete("/produtos/{id}");
    }
}