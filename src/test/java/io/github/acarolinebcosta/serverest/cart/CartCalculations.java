package io.github.acarolinebcosta.serverest.cart;

import io.github.acarolinebcosta.serverest.product.CreatedProduct;

import java.util.List;

public final class CartCalculations {

    public static int totalQuantity(CartRequest request) {
        return request.products().stream()
                .mapToInt(CartItemRequest::quantity)
                .reduce(0, Math::addExact);
    }

    public static long totalPrice(CartRequest request, List<CreatedProduct> products) {
        return request.products().stream()
                .mapToLong(item -> {
                    CreatedProduct product = requireProduct(products, item.productId());

                    return Math.multiplyExact(
                            (long) product.request().price(),
                            item.quantity().longValue()
                    );
                })
                .reduce(0L, Math::addExact);
    }

    public static int stockAfterReservation(int initialStock, int requestedQuantity) {
        return Math.subtractExact(initialStock, requestedQuantity);
    }

    private static CreatedProduct requireProduct(List<CreatedProduct> products, String productId) {
        return products.stream()
                .filter(created -> created.id().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "O produto não foi criado no cenário: " + productId
                ));
    }

    private CartCalculations() {
        throw new UnsupportedOperationException("Utility class");
    }
}