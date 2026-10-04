package io.github.acarolinebcosta.serverest.cart;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.product.ProductService;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public final class PurchaseSteps {

    private final ScenarioContext context;
    private final ProductService productService;
    private final CartService cartService;
    private final CartAssertions cartAssertions;
    private final CartStockAssertions stockAssertions;

    @Quando("ele realiza uma compra")
    public void completePurchase() {
        reserveMainProduct();
        ResponseAssertions.assertMessage(cartService.completePurchase(), 200,
                ApiMessages.DELETE_SUCCESS, "Complete purchase");
    }

    @Quando("ele cria e cancela uma compra")
    public void cancelPurchase() {
        reserveMainProduct();
        ResponseAssertions.assertMessage(cartService.cancelPurchase(), 200,
                ApiMessages.CANCEL_SUCCESS, "Cancel purchase");
    }

    @Entao("a compra deve ser concluída mantendo o estoque consumido")
    public void purchaseShouldKeepConsumedStock() {
        cartAssertions.assertCartAbsent();
        CartRequest activeRequest = context.getActiveCartRequest();
        assertThat(activeRequest).as("Successful cart request must be stored").isNotNull();
        stockAssertions.assertReserved(activeRequest);
    }

    @Entao("o carrinho deve ser removido e o estoque restaurado")
    public void cancellationShouldRemoveCartAndRestoreStock() {
        cartAssertions.assertCartAbsent();
        stockAssertions.assertRestored();
    }

    private void reserveMainProduct() {
        CartRequest request = new CartBuilder()
                .addItem(productService.getByAlias("principal").id(), 3).build();
        cartAssertions.assertCreation(cartService.create(request));
        cartAssertions.assertPersistedCart(request);
        stockAssertions.assertReserved(request);
    }
}
