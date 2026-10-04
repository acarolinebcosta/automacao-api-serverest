package io.github.acarolinebcosta.serverest.product;

public record CreatedProduct(
        String alias,
        String id,
        ProductRequest request
) {
    public int initialStock() {
        return request.quantity();
    }
}
