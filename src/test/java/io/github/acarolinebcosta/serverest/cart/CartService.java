package io.github.acarolinebcosta.serverest.cart;

import io.github.acarolinebcosta.serverest.api.CreateResponse;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CartService {

    private final ScenarioContext context;
    private final CartClient cartClient;

    @Step("Create cart")
    public Response create(CartRequest request) {
        Response response = cartClient.create(context.getToken(), request);
        if (response.statusCode() == 201) {
            context.setCartId(response.as(CreateResponse.class).id());
            context.setActiveCartRequest(request);
        }
        return response;
    }

    public Response findByUser() {
        return cartClient.findByUserId(context.getUserId());
    }

    public Response findById(String cartId) {
        return cartClient.findById(cartId);
    }

    @Step("Complete purchase")
    public Response completePurchase() {
        return cartClient.completePurchase(context.getToken());
    }

    @Step("Cancel purchase")
    public Response cancelPurchase() {
        return cartClient.cancelPurchase(context.getToken());
    }
}
