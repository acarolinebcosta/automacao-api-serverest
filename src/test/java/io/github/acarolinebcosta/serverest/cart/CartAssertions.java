package io.github.acarolinebcosta.serverest.cart;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.api.CreateResponse;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.evidence.EvidenceSanitizer;
import io.github.acarolinebcosta.serverest.product.CreatedProduct;
import io.github.acarolinebcosta.serverest.validation.ContractAssertions;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public final class CartAssertions {

    private final ScenarioContext context;
    private final CartService cartService;
    private final EvidenceSanitizer evidenceSanitizer;
    private final ContractAssertions contractAssertions;

    @Step("Validar criação do carrinho")
    public void assertCreation(Response response) {
        ResponseAssertions.assertMessage(
                response,
                201,
                ApiMessages.CREATE_SUCCESS,
                "Criar carrinho"
        );

        CreateResponse createdCart = response.as(CreateResponse.class);

        assertThat(createdCart.id())
                .as("ID do carrinho criado")
                .isNotBlank();

        assertThat(context.getCartId())
                .as("ID do carrinho armazenado no contexto do cenário")
                .isEqualTo(createdCart.id());
    }

    @Step("Validar rejeição da criação do carrinho")
    public void assertRejection(Response response, String expectedMessage) {
        ResponseAssertions.assertMessage(
                response,
                400,
                expectedMessage,
                "Rejeitar criação de carrinho inválido"
        );

        assertThat(response.jsonPath().getString("_id"))
                .as("Uma criação de carrinho rejeitada não deve retornar ID")
                .isNull();
    }

    @Step("Validar carrinho persistido")
    public CartResponse assertPersistedCart(CartRequest expectedRequest) {
        CartListResponse cartList = fetchCartList(
                cartService.findByUser(),
                "Buscar carrinho por usuário"
        );

        assertThat(cartList.carts())
                .as("Carrinhos pertencentes ao usuário do cenário")
                .hasSize(1);

        assertThat(cartList.quantity())
                .as("Quantidade de carrinhos retornados")
                .isEqualTo(cartList.carts().size());

        CartResponse cart = cartList.carts().getFirst();

        assertThat(cart.id())
                .as("ID do carrinho persistido")
                .isEqualTo(context.getCartId());

        assertOwner(cart);
        assertItems(cart, expectedRequest);
        assertTotals(cart, expectedRequest);

        return cart;
    }

    @Step("Validar proprietário do carrinho")
    public void assertOwner(CartResponse cart) {
        assertThat(cart.userId())
                .as("Proprietário do carrinho")
                .isEqualTo(context.getUserId());
    }

    @Step("Validar itens do carrinho")
    public void assertItems(CartResponse cart, CartRequest expectedRequest) {
        assertThat(cart.products())
                .as("Produtos do carrinho")
                .hasSize(expectedRequest.products().size());

        assertThat(cart.products())
                .extracting(CartItemResponse::productId)
                .as("IDs dos produtos persistidos no carrinho")
                .containsExactlyInAnyOrderElementsOf(
                        expectedRequest.products().stream()
                                .map(CartItemRequest::productId)
                                .toList()
                );

        for (CartItemRequest expectedItem : expectedRequest.products()) {
            CartItemResponse actualItem = cart.products().stream()
                    .filter(item -> item.productId().equals(expectedItem.productId()))
                    .findFirst()
                    .orElseThrow();

            CreatedProduct product = findCreatedProduct(expectedItem.productId());

            assertThat(actualItem.quantity())
                    .as("Quantidade do produto %s", expectedItem.productId())
                    .isEqualTo(expectedItem.quantity());

            assertThat(actualItem.unitPrice())
                    .as("Preço unitário do produto %s", expectedItem.productId())
                    .isEqualTo(product.request().price());
        }
    }

    @Step("Validar totais do carrinho")
    public void assertTotals(CartResponse cart, CartRequest expectedRequest) {
        int expectedQuantity = CartCalculations.totalQuantity(expectedRequest);
        long expectedPrice = CartCalculations.totalPrice(expectedRequest, context.getProducts());

        Allure.addAttachment(
                "Totais esperados do carrinho",
                "application/json",
                evidenceSanitizer.sanitize(new ExpectedTotals(expectedQuantity, expectedPrice)),
                ".json"
        );

        assertThat(cart.totalQuantity())
                .as("Quantidade total")
                .isEqualTo(expectedQuantity);

        assertThat(cart.totalPrice())
                .as("O preço total deve ser retornado")
                .isNotNull();

        assertThat(cart.totalPrice().longValue())
                .as("Preço total")
                .isEqualTo(expectedPrice);
    }

    @Step("Validar ausência do carrinho por usuário e ID criado")
    public void assertCartAbsent() {
        assertEmptyCartList(
                fetchCartList(cartService.findByUser(), "Buscar carrinhos por usuário"),
                "O usuário não deve possuir carrinho ativo"
        );

        if (context.getCartId() != null) {
            assertEmptyCartList(
                    fetchCartList(cartService.findById(context.getCartId()), "Buscar carrinho removido por ID"),
                    "O ID do carrinho removido não deve existir"
            );
        }
    }

    private CartListResponse fetchCartList(Response response, String action) {
        ResponseAssertions.assertStatus(response, 200, action);
        contractAssertions.validateCarts(response);
        return response.as(CartListResponse.class);
    }

    private void assertEmptyCartList(CartListResponse cartList, String message) {
        assertThat(cartList.carts())
                .as(message)
                .isEmpty();

        assertThat(cartList.quantity())
                .as("%s - quantidade", message)
                .isZero();
    }

    private CreatedProduct findCreatedProduct(String productId) {
        return context.getProducts().stream()
                .filter(product -> product.id().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "O produto não foi criado no cenário: " + productId
                ));
    }

    private record ExpectedTotals(int totalQuantity, long totalPrice) {
    }
}