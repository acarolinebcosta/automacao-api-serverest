package io.github.acarolinebcosta.serverest.cart;

import io.github.acarolinebcosta.serverest.api.CreateResponse;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

/**
 * Application service for the cart domain.
 *
 * Orchestrates calls to {@link CartClient} and updates the {@link ScenarioContext}
 * with the created cart ID and the active cart request so later steps and
 * assertions can validate persistence, stock reservation and totals.
 *
 * Authentication comes from the scenario context. The token is populated during
 * authentication and reused across cart operations.
 */
@RequiredArgsConstructor
public final class CartService {

    private final ScenarioContext context;
    private final CartClient cartClient;

    @Step("Criar carrinho")
    public Response create(CartRequest request) {
        Response response = cartClient.create(
                context.getToken(),
                request
        );

        if (response.statusCode() == 201) {
            context.setCartId(
                    response.as(CreateResponse.class).id()
            );
            context.setActiveCartRequest(request);
        }

        return response;
    }

    public Response findByUser() {
        return cartClient.findByUserId(
                context.getUserId()
        );
    }

    public Response findById(String cartId) {
        return cartClient.findById(cartId);
    }

    @Step("Concluir compra")
    public Response completePurchase() {
        return cartClient.completePurchase(
                context.getToken()
        );
    }

    @Step("Cancelar compra")
    public Response cancelPurchase() {
        return cartClient.cancelPurchase(
                context.getToken()
        );
    }
}