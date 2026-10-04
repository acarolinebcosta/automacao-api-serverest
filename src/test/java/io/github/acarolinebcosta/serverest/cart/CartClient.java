package io.github.acarolinebcosta.serverest.cart;

import io.github.acarolinebcosta.serverest.api.ApiConfig;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CartClient {

    private final ApiConfig config;

    public Response create(String token, CartRequest cart) {
        return config.authenticated(token).body(cart).when().post("/carrinhos");
    }

    public Response findByUserId(String userId) {
        return config.request().queryParam("idUsuario", userId).when().get("/carrinhos");
    }

    public Response findById(String cartId) {
        return config.request().queryParam("_id", cartId).when().get("/carrinhos");
    }

    public Response completePurchase(String token) {
        return config.authenticated(token).when().delete("/carrinhos/concluir-compra");
    }

    public Response cancelPurchase(String token) {
        return config.authenticated(token).when().delete("/carrinhos/cancelar-compra");
    }
}
