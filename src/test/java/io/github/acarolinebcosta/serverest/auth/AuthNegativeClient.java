package io.github.acarolinebcosta.serverest.auth;

import io.github.acarolinebcosta.serverest.api.ApiConfig;
import io.github.acarolinebcosta.serverest.cart.CartRequest;
import io.github.acarolinebcosta.serverest.product.ProductRequest;
import io.restassured.response.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class AuthNegativeClient {

    @NonNull
    private final ApiConfig config;

    public Response createProductWithoutToken(ProductRequest product) {
        return config.request()
                .body(product)
                .when()
                .post("/produtos");
    }

    public Response createProductWithToken(
            String invalidToken,
            ProductRequest product
    ) {
        return config.request()
                .header("Authorization", invalidToken)
                .body(product)
                .when()
                .post("/produtos");
    }

    public Response deleteProductWithoutToken(String productId) {
        return config.request()
                .pathParam("id", productId)
                .when()
                .delete("/produtos/{id}");
    }

    public Response createCartWithoutToken(CartRequest cart) {
        return config.request()
                .body(cart)
                .when()
                .post("/carrinhos");
    }

    public Response completePurchaseWithoutToken() {
        return config.request()
                .when()
                .delete("/carrinhos/concluir-compra");
    }

    public Response cancelPurchaseWithoutToken() {
        return config.request()
                .when()
                .delete("/carrinhos/cancelar-compra");
    }
}