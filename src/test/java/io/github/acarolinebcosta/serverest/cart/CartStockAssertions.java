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

    @Step("Validar estoque reservado ou consumido")
    public void assertReserved(CartRequest request) {
        for (CartItemRequest item : request.products()) {
            CreatedProduct product = findProduct(item.productId());
            int expectedStock = CartCalculations.stockAfterReservation(
                    product.initialStock(),
                    item.quantity()
            );
            assertStock(product, item.quantity(), expectedStock);
        }
    }

    @Step("Validar estoque restaurado")
    public void assertRestored() {
        assertInitial(context.getActiveCartRequest());
    }

    @Step("Validar estoque inalterado para todos os produtos criados")
    public void assertUnchanged(CartRequest request) {
        assertInitial(request);
    }

    private void assertInitial(CartRequest request) {
        assertThat(request)
                .as("A requisição do carrinho deve estar disponível para evidência de estoque")
                .isNotNull();

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

        Allure.addAttachment(
                "Estoque esperado e observado do produto",
                "application/json",
                evidenceSanitizer.sanitize(new StockState(
                        product.id(),
                        product.initialStock(),
                        requestedQuantity,
                        expectedStock,
                        actualStock
                )),
                ".json"
        );

        assertThat(actualStock)
                .as("Estoque do produto %s: inicial=%d, solicitado=%d, esperado=%d",
                        product.id(),
                        product.initialStock(),
                        requestedQuantity,
                        expectedStock)
                .isEqualTo(expectedStock);
    }

    private CreatedProduct findProduct(String productId) {
        return context.getProducts().stream()
                .filter(product -> product.id().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "O produto não foi criado no cenário: " + productId
                ));
    }

    private record StockState(
            String productId,
            int initialStock,
            int requestedQuantity,
            int expectedStock,
            Integer actualStock
    ) {
    }
}