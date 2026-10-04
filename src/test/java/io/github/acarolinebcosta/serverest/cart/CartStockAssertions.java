package io.github.acarolinebcosta.serverest.cart;

import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.evidence.EvidenceSanitizer;
import io.github.acarolinebcosta.serverest.product.CreatedProduct;
import io.github.acarolinebcosta.serverest.product.ProductService;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public final class CartStockAssertions {

    private final ScenarioContext context;
    private final ProductService productService;
    private final EvidenceSanitizer evidenceSanitizer;

    @Step("Validate reserved or consumed stock")
    public void assertReserved(CartRequest request) {
        for (CartItemRequest item : request.products()) {
            CreatedProduct product = findProduct(item.productId());
            int expectedStock = CartCalculations.stockAfterReservation(product.initialStock(), item.quantity());
            assertStock(product, item.quantity(), expectedStock);
        }
    }

    @Step("Validate restored stock")
    public void assertRestored() {
        assertInitial(context.getActiveCartRequest());
    }

    @Step("Validate unchanged stock for every created product")
    public void assertUnchanged(CartRequest request) {
        assertInitial(request);
    }

    private void assertInitial(CartRequest request) {
        assertThat(request).as("Cart request must be available for stock evidence").isNotNull();
        for (CreatedProduct product : context.getProducts()) {
            int requestedQuantity = request.products().stream()
                    .filter(item -> item.productId().equals(product.id()))
                    .mapToInt(CartItemRequest::quantity)
                    .reduce(0, Math::addExact);
            assertStock(product, requestedQuantity, product.initialStock());
        }
    }

    private void assertStock(CreatedProduct product, int requestedQuantity, int expectedStock) {
        Integer actualStock = productService.findProduct(product).quantity();
        Allure.addAttachment("Expected and observed product stock", "application/json",
                evidenceSanitizer.sanitize(new StockState(product.id(), product.initialStock(),
                        requestedQuantity, expectedStock, actualStock)), ".json");
        assertThat(actualStock)
                .as("Product %s stock: initial=%d, requested=%d, expected=%d",
                        product.id(), product.initialStock(), requestedQuantity, expectedStock)
                .isEqualTo(expectedStock);
    }

    private CreatedProduct findProduct(String productId) {
        return context.getProducts().stream()
                .filter(product -> product.id().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Product was not created in the scenario: " + productId));
    }

    private record StockState(String productId, int initialStock, int requestedQuantity,
                              int expectedStock, Integer actualStock) {
    }
}
