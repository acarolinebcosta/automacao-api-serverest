package io.github.acarolinebcosta.serverest.cart;

import io.github.acarolinebcosta.serverest.api.ApiConfig;
import io.restassured.response.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * HTTP client for the ServeRest cart resource.
 *
 * Encapsulates calls to the {@code /carrinhos} endpoints without orchestrating
 * business flows or performing response validations. These responsibilities
 * are handled by {@code CartService}, {@code CartAssertions} and
 * {@code ContractAssertions}.
 *
 * The following operations require an authentication token:
 * {@link #create(String, CartRequest)}, {@link #completePurchase(String)} and
 * {@link #cancelPurchase(String)}. Queries by user or cart ID are public.
 */
@RequiredArgsConstructor
public final class CartClient {

    @NonNull
    private final ApiConfig config;

    public Response create(String token, CartRequest cart) {
        return config.authenticated(token)
                .body(cart)
                .when()
                .post("/carrinhos");
    }

    public Response findByUserId(String userId) {
        return config.request()
                .queryParam("idUsuario", userId)
                .when()
                .get("/carrinhos");
    }

    public Response findById(String cartId) {
        return config.request()
                .queryParam("_id", cartId)
                .when()
                .get("/carrinhos");
    }

    public Response completePurchase(String token) {
        return config.authenticated(token)
                .when()
                .delete("/carrinhos/concluir-compra");
    }

    public Response cancelPurchase(String token) {
        return config.authenticated(token)
                .when()
                .delete("/carrinhos/cancelar-compra");
    }
}