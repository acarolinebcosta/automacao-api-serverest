package io.github.acarolinebcosta.serverest.product;

import io.github.acarolinebcosta.serverest.api.ApiConfig;
import io.restassured.response.Response;

import java.util.Objects;

public final class ProductClient {

    private final ApiConfig config;

    public ProductClient(ApiConfig config) {
        this.config = Objects.requireNonNull(config, "API configuration is required");
    }

    public Response create(String token, ProductRequest product) {
        return config.authenticated(token).body(product).when().post("/produtos");
    }

    public Response findById(String id) {
        return config.request().pathParam("id", id).when().get("/produtos/{id}");
    }

    public Response delete(String token, String id) {
        return config.authenticated(token).pathParam("id", id).when().delete("/produtos/{id}");
    }
}
