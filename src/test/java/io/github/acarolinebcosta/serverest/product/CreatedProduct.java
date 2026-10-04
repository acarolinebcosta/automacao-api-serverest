package io.github.acarolinebcosta.serverest.product;

public record CreatedProduct(String alias, String id, ProductRequest request, int initialStock) {
}
