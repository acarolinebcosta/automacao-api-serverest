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

    public void assertCreation(Response response) {
        ResponseAssertions.assertMessage(response, 201, ApiMessages.CREATE_SUCCESS, "Create cart");
        CreateResponse createdCart = response.as(CreateResponse.class);

        assertThat(createdCart.id()).as("Created cart ID").isNotBlank();
        assertThat(context.getCartId())
                .as("Cart ID stored in scenario context")
                .isEqualTo(createdCart.id());
    }

    public void assertRejection(Response response, String expectedMessage) {
        ResponseAssertions.assertMessage(response, 400, expectedMessage, "Reject invalid cart creation");
        assertThat(response.jsonPath().getString("_id"))
                .as("Rejected cart creation must not return an ID")
                .isNull();
    }

    @Step("Validate persisted cart")
    public CartResponse assertPersistedCart(CartRequest expectedRequest) {
        CartListResponse cartList = fetchCartList(cartService.findByUser(), "Find cart by user");
        assertThat(cartList.carts()).as("Carts belonging to the scenario user").hasSize(1);
        assertThat(cartList.quantity()).as("Number of carts returned").isEqualTo(cartList.carts().size());

        CartResponse cart = cartList.carts().getFirst();
        assertThat(cart.id()).as("Persisted cart ID").isEqualTo(context.getCartId());
        assertOwner(cart);
        assertItems(cart, expectedRequest);
        assertTotals(cart, expectedRequest);
        return cart;
    }

    @Step("Validate cart owner")
    public void assertOwner(CartResponse cart) {
        assertThat(cart.userId()).as("Cart owner").isEqualTo(context.getUserId());
    }

    @Step("Validate cart items")
    public void assertItems(CartResponse cart, CartRequest expectedRequest) {
        assertThat(cart.products())
                .as("Cart products")
                .hasSize(expectedRequest.products().size());
        assertThat(cart.products()).extracting(CartItemResponse::productId)
                .as("Product IDs persisted in cart")
                .containsExactlyInAnyOrderElementsOf(expectedRequest.products().stream()
                        .map(CartItemRequest::productId).toList());

        for (CartItemRequest expectedItem : expectedRequest.products()) {
            CartItemResponse actualItem = cart.products().stream()
                    .filter(item -> item.productId().equals(expectedItem.productId()))
                    .findFirst().orElseThrow();
            CreatedProduct product = findCreatedProduct(expectedItem.productId());

            assertThat(actualItem.quantity())
                    .as("Quantity for product %s", expectedItem.productId())
                    .isEqualTo(expectedItem.quantity());
            assertThat(actualItem.unitPrice())
                    .as("Unit price for product %s", expectedItem.productId())
                    .isEqualTo(product.request().price());
        }
    }

    @Step("Validate cart totals")
    public void assertTotals(CartResponse cart, CartRequest expectedRequest) {
        int expectedQuantity = CartCalculations.totalQuantity(expectedRequest);
        long expectedPrice = CartCalculations.totalPrice(expectedRequest, context.getProducts());
        Allure.addAttachment("Expected cart totals", "application/json",
                evidenceSanitizer.sanitize(new ExpectedTotals(expectedQuantity, expectedPrice)), ".json");

        assertThat(cart.totalQuantity()).as("Total quantity").isEqualTo(expectedQuantity);
        assertThat(cart.totalPrice()).as("Total price must be returned").isNotNull();
        assertThat(cart.totalPrice().longValue()).as("Total price").isEqualTo(expectedPrice);
    }

    @Step("Validate cart absence by user and created ID")
    public void assertCartAbsent() {
        assertEmptyCartList(fetchCartList(cartService.findByUser(), "Find carts by user"),
                "User must not have an active cart");
        if (context.getCartId() != null) {
            assertEmptyCartList(fetchCartList(cartService.findById(context.getCartId()), "Find removed cart by ID"),
                    "Removed cart ID must not exist");
        }
    }

    private CartListResponse fetchCartList(Response response, String action) {
        ResponseAssertions.assertStatus(response, 200, action);
        contractAssertions.validateCarts(response);
        return response.as(CartListResponse.class);
    }

    private void assertEmptyCartList(CartListResponse cartList, String message) {
        assertThat(cartList.carts()).as(message).isEmpty();
        assertThat(cartList.quantity()).as("%s - quantity", message).isZero();
    }

    private CreatedProduct findCreatedProduct(String productId) {
        return context.getProducts().stream()
                .filter(product -> product.id().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Product was not created in the scenario: " + productId));
    }

    private record ExpectedTotals(int totalQuantity, long totalPrice) {
    }
}
