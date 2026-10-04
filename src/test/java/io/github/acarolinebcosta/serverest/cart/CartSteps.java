package io.github.acarolinebcosta.serverest.cart;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.product.CreatedProduct;
import io.github.acarolinebcosta.serverest.product.ProductAliases;
import io.github.acarolinebcosta.serverest.product.ProductService;
import io.github.acarolinebcosta.serverest.testdata.DataGenerator;
import io.github.acarolinebcosta.serverest.user.UserService;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public final class CartSteps {

    private final ScenarioContext context;
    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final CartAssertions cartAssertions;
    private final CartStockAssertions stockAssertions;

    private Response creationResponse;
    private CartRequest currentRequest;
    private String expectedRejection;

    @Dado("que o usuário já possui um carrinho")
    public void userAlreadyHasCart() {
        userService.createAuthenticatedAdmin();
        productService.createProduct(ProductAliases.MAIN, 100, 10);
        requestCartWithMainProduct(3);
        assertSuccessfulCreation();
        stockAssertions.assertReserved(currentRequest);
    }

    @Quando("tenta criar outro carrinho")
    public void tryToCreateSecondCart() {
        sendRequest(mainProductRequest(2), ApiMessages.SECOND_CART);
    }

    @Quando("tenta criar um carrinho com produto inexistente")
    public void tryToCreateCartWithNonexistentProduct() {
        String productId = DataGenerator.nonexistentProductId();
        productService.assertNonexistent(productId);
        sendRequest(
                new CartBuilder().addItem(productId, 1).build(),
                ApiMessages.PRODUCT_NOT_FOUND
        );
    }

    @Quando("o usuário solicita uma quantidade superior ao estoque")
    public void requestMoreThanAvailableStock() {
        CreatedProduct product = productService.getByAlias(ProductAliases.MAIN);
        int quantity = Math.addExact(product.initialStock(), 1);
        requestCartWithMainProduct(quantity);
    }

    @Quando("o usuário solicita {int} unidades")
    public void requestCartWithMainProduct(int quantity) {
        sendRequest(mainProductRequest(quantity), ApiMessages.INSUFFICIENT_STOCK);
    }

    @Quando("o usuário informa o mesmo produto duas vezes no carrinho")
    public void tryToCreateCartWithDuplicatedProduct() {
        CreatedProduct product = productService.getByAlias(ProductAliases.MAIN);
        sendRequest(
                new CartBuilder()
                        .addItem(product.id(), 2)
                        .addItem(product.id(), 3)
                        .build(),
                ApiMessages.PRODUCT_DUPLICATED
        );
    }

    @Quando("o usuário adiciona múltiplos produtos ao carrinho")
    public void requestCartWithMultipleProducts() {
        requestTwoProducts(2, 3);
    }

    @Quando("o usuário solicita múltiplos produtos excedendo um dos estoques")
    public void requestMultipleProductsExceedingOneStock() {
        CreatedProduct secondProduct = productService.getByAlias(ProductAliases.MULTI_B);
        int excessiveQuantity = Math.addExact(secondProduct.initialStock(), 1);
        requestTwoProducts(2, excessiveQuantity);
    }

    @Entao("a operação deve ser rejeitada preservando o primeiro carrinho")
    public void secondCartShouldBeRejected() {
        cartAssertions.assertRejection(requireCreationResponse(), expectedRejection);

        CartRequest firstRequest = context.getActiveCartRequest();

        assertThat(firstRequest)
                .as("A requisição do primeiro carrinho deve ser preservada")
                .isNotNull();

        cartAssertions.assertPersistedCart(firstRequest);
        stockAssertions.assertReserved(firstRequest);
    }

    @Entao("a operação deve ser rejeitada sem criar carrinho")
    public void creationShouldBeRejectedWithoutCart() {
        assertRejectedCreation();
    }

    @Entao("a operação deve ser rejeitada sem alterar o estoque")
    public void creationShouldBeRejectedWithoutChangingAnyStock() {
        assertRejectedCreation();
        stockAssertions.assertUnchanged(requireCurrentRequest());
    }

    @Entao("a criação deve resultar em {string}")
    public void boundaryResultShouldBe(String expectedResult) {
        switch (expectedResult) {
            case "sucesso" -> assertSuccessfulCreationAndCancellation();
            case "rejeitado" -> creationShouldBeRejectedWithoutChangingAnyStock();
            default -> throw new IllegalArgumentException(
                    "Resultado de boundary não suportado: " + expectedResult
            );
        }
    }

    @Entao("o carrinho deve calcular os totais e estoques corretamente")
    public void multipleProductTotalsAndStocksShouldBeCorrect() {
        assertSuccessfulCreationAndCancellation();
    }

    private void assertSuccessfulCreationAndCancellation() {
        assertSuccessfulCreation();
        stockAssertions.assertReserved(requireCurrentRequest());

        ResponseAssertions.assertMessage(
                cartService.cancelPurchase(),
                200,
                ApiMessages.CANCEL_SUCCESS,
                "Cancelar compra"
        );

        cartAssertions.assertCartAbsent();
        stockAssertions.assertRestored();
    }

    private void assertSuccessfulCreation() {
        cartAssertions.assertCreation(requireCreationResponse());
        cartAssertions.assertPersistedCart(requireCurrentRequest());
    }

    private void assertRejectedCreation() {
        cartAssertions.assertRejection(requireCreationResponse(), expectedRejection);
        cartAssertions.assertCartAbsent();
    }

    private void requestTwoProducts(int firstQuantity, int secondQuantity) {
        CreatedProduct first = productService.getByAlias(ProductAliases.MULTI_A);
        CreatedProduct second = productService.getByAlias(ProductAliases.MULTI_B);

        sendRequest(
                new CartBuilder()
                        .addItem(first.id(), firstQuantity)
                        .addItem(second.id(), secondQuantity)
                        .build(),
                ApiMessages.INSUFFICIENT_STOCK
        );
    }

    private CartRequest mainProductRequest(int quantity) {
        return new CartBuilder()
                .addItem(productService.getByAlias(ProductAliases.MAIN).id(), quantity)
                .build();
    }

    private void sendRequest(CartRequest request, String rejectionMessage) {
        currentRequest = request;
        expectedRejection = rejectionMessage;
        creationResponse = cartService.create(request);
    }

    private Response requireCreationResponse() {
        assertThat(creationResponse)
                .as("A resposta de criação do carrinho deve existir")
                .isNotNull();

        return creationResponse;
    }

    private CartRequest requireCurrentRequest() {
        assertThat(currentRequest)
                .as("A requisição do carrinho deve ser preparada")
                .isNotNull();

        return currentRequest;
    }
}