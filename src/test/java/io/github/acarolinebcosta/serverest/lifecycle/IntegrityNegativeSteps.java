package io.github.acarolinebcosta.serverest.lifecycle;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.cart.CartBuilder;
import io.github.acarolinebcosta.serverest.cart.CartRequest;
import io.github.acarolinebcosta.serverest.cart.CartService;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.product.CreatedProduct;
import io.github.acarolinebcosta.serverest.product.ProductAliases;
import io.github.acarolinebcosta.serverest.product.ProductService;
import io.github.acarolinebcosta.serverest.user.UserClient;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IntegrityNegativeSteps {

    private final ScenarioContext context;
    private final ProductService productService;
    private final CartService cartService;
    private final UserClient userClient;

    @Quando("o usuário possui um carrinho ativo com o produto principal")
    public void activeCartWithMainProduct() {
        CreatedProduct mainProduct = productService.getByAlias(ProductAliases.MAIN);

        CartRequest request = new CartBuilder()
                .addItem(mainProduct.id(), 1)
                .build();

        context.setLastResponse(cartService.create(request));
        context.setActiveCartRequest(request);
    }

    @Quando("tento excluir o produto principal que está em uso")
    public void attemptDeleteProductInUse() {
        CreatedProduct mainProduct = productService.getByAlias(ProductAliases.MAIN);
        context.setLastResponse(productService.deleteProduct(mainProduct));
    }

    @Quando("tento excluir o usuário que possui carrinho")
    public void attemptDeleteUserWithCart() {
        context.setLastResponse(userClient.delete(context.getUserId()));
    }

    @Entao("a mensagem de produto em uso deve ser retornada")
    public void productInUseMessageShouldBeReturned() {
        ResponseAssertions.assertMessage(
                context.getLastResponse(),
                400,
                ApiMessages.PRODUCT_IN_USE,
                "Produto em uso"
        );
    }

    @Entao("a mensagem de usuário com carrinho deve ser retornada")
    public void userWithCartMessageShouldBeReturned() {
        ResponseAssertions.assertMessage(
                context.getLastResponse(),
                400,
                ApiMessages.USER_WITH_CART,
                "Usuário com carrinho"
        );
    }
}