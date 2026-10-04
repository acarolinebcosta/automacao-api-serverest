package io.github.acarolinebcosta.serverest.lifecycle;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.cart.CartClient;
import io.github.acarolinebcosta.serverest.cart.CartListResponse;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.product.ProductClient;
import io.github.acarolinebcosta.serverest.user.UserClient;
import io.github.acarolinebcosta.serverest.validation.ContractAssertions;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public final class CleanupAssertions {

    private final ScenarioContext context;
    private final CartClient cartClient;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final ContractAssertions contractAssertions;

    @Step("Confirmar ausência do carrinho após a limpeza")
    public void assertCartAbsent() {
        assertEmptyCartList(
                cartClient.findByUserId(context.getUserId()),
                "Buscar carrinhos por usuário após a limpeza"
        );

        if (context.getCartId() != null) {
            assertEmptyCartList(
                    cartClient.findById(context.getCartId()),
                    "Buscar carrinho por ID após a limpeza"
            );
        }
    }

    @Step("Confirmar ausência do produto após a limpeza")
    public void assertProductAbsent(String productId) {
        ResponseAssertions.assertMessage(
                productClient.findById(productId),
                400,
                ApiMessages.PRODUCT_NOT_FOUND,
                "Confirmar ausência do produto"
        );
    }

    @Step("Confirmar ausência do usuário após a limpeza")
    public void assertUserAbsent() {
        ResponseAssertions.assertMessage(
                userClient.findById(context.getUserId()),
                400,
                ApiMessages.USER_NOT_FOUND,
                "Confirmar ausência do usuário"
        );
    }

    private void assertEmptyCartList(Response response, String action) {
        ResponseAssertions.assertStatus(response, 200, action);
        contractAssertions.validateCarts(response);

        CartListResponse carts = response.as(CartListResponse.class);

        assertThat(carts.carts())
                .as("%s: carrinhos persistidos", action)
                .isEmpty();

        assertThat(carts.quantity())
                .as("%s: quantidade de carrinhos", action)
                .isZero();
    }
}