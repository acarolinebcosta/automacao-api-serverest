package io.github.acarolinebcosta.serverest.cart;

import io.github.acarolinebcosta.serverest.api.CreateResponse;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

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

    @Step("Tentar cadastrar carrinho sem produtos")
    public Response attemptCreateWithoutProducts() {
        return cartClient.createRaw(
                context.getToken(),
                Map.of()
        );
    }

    @Step("Tentar cadastrar carrinho com quantidade zero")
    public Response attemptCreateWithZeroQuantity(String productId) {
        return cartClient.create(
                context.getToken(),
                new CartRequest(List.of(
                        new CartItemRequest(productId, 0)
                ))
        );
    }

    @Step("Tentar cadastrar carrinho com quantidade negativa")
    public Response attemptCreateWithNegativeQuantity(String productId) {
        return cartClient.create(
                context.getToken(),
                new CartRequest(List.of(
                        new CartItemRequest(productId, -1)
                ))
        );
    }

    @Step("Tentar cadastrar carrinho com produto de ID vazio")
    public Response attemptCreateWithEmptyProductId() {
        return cartClient.create(
                context.getToken(),
                new CartRequest(List.of(
                        new CartItemRequest("", 1)
                ))
        );
    }
}