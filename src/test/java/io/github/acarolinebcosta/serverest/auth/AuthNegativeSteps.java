package io.github.acarolinebcosta.serverest.auth;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.cart.CartRequest;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.product.ProductData;
import io.github.acarolinebcosta.serverest.product.ProductRequest;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class AuthNegativeSteps {

    private static final String INVALID_TOKEN =
            "Bearer token-invalido-para-teste";

    private static final String WELL_FORMED_UNKNOWN_ID =
            "aBcDeFgHiJkLmNoP";

    private static final int PRODUCT_PRICE = 100;
    private static final int PRODUCT_STOCK = 10;

    private final ScenarioContext context;
    private final AuthNegativeClient authNegativeClient;

    @Quando("tento criar um produto sem token")
    public void attemptCreateProductWithoutToken() {
        ProductRequest product = ProductData.valid(
                PRODUCT_PRICE,
                PRODUCT_STOCK
        );

        context.setLastResponse(
                authNegativeClient.createProductWithoutToken(product)
        );
    }

    @Quando("tento criar um produto com token inválido")
    public void attemptCreateProductWithInvalidToken() {
        ProductRequest product = ProductData.valid(
                PRODUCT_PRICE,
                PRODUCT_STOCK
        );

        context.setLastResponse(
                authNegativeClient.createProductWithToken(
                        INVALID_TOKEN,
                        product
                )
        );
    }

    @Quando("tento excluir um produto sem token")
    public void attemptDeleteProductWithoutToken() {
        context.setLastResponse(
                authNegativeClient.deleteProductWithoutToken(
                        WELL_FORMED_UNKNOWN_ID
                )
        );
    }

    @Quando("tento criar um carrinho sem token")
    public void attemptCreateCartWithoutToken() {
        CartRequest request = new CartRequest(List.of());

        context.setLastResponse(
                authNegativeClient.createCartWithoutToken(request)
        );
    }

    @Quando("tento concluir uma compra sem token")
    public void attemptCompletePurchaseWithoutToken() {
        context.setLastResponse(
                authNegativeClient.completePurchaseWithoutToken()
        );
    }

    @Quando("tento cancelar uma compra sem token")
    public void attemptCancelPurchaseWithoutToken() {
        context.setLastResponse(
                authNegativeClient.cancelPurchaseWithoutToken()
        );
    }

    @Entao("a mensagem de token inválido deve ser retornada")
    public void invalidTokenMessageShouldBeReturned() {
        ResponseAssertions.assertMessage(
                context.getLastResponse(),
                401,
                ApiMessages.INVALID_TOKEN,
                "Validar mensagem de token inválido"
        );
    }
}