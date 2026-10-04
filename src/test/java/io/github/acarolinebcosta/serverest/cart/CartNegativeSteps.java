package io.github.acarolinebcosta.serverest.cart;

import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.product.ProductAliases;
import io.github.acarolinebcosta.serverest.product.ProductService;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CartNegativeSteps {

    private final CartService cartService;
    private final ProductService productService;
    private final ScenarioContext context;

    @Quando("tento cadastrar um carrinho sem produtos")
    public void attemptCreateCartWithoutProducts() {
        context.setLastResponse(cartService.attemptCreateWithoutProducts());
    }

    @Quando("tento cadastrar um carrinho com quantidade zero")
    public void attemptCreateCartWithZeroQuantity() {
        String productId = productService.getByAlias(ProductAliases.MAIN).id();

        context.setLastResponse(
                cartService.attemptCreateWithZeroQuantity(productId)
        );
    }

    @Quando("tento cadastrar um carrinho com quantidade negativa")
    public void attemptCreateCartWithNegativeQuantity() {
        String productId = productService.getByAlias(ProductAliases.MAIN).id();

        context.setLastResponse(
                cartService.attemptCreateWithNegativeQuantity(productId)
        );
    }

    @Quando("tento cadastrar um carrinho com produto de ID vazio")
    public void attemptCreateCartWithEmptyProductId() {
        context.setLastResponse(cartService.attemptCreateWithEmptyProductId());
    }

    @Entao("a criação do carrinho deve ser rejeitada com status 400")
    public void cartCreationShouldBeRejected() {
        ResponseAssertions.assertStatus(
                context.getLastResponse(),
                400,
                "Rejeitar criação de carrinho"
        );
    }
}