package io.github.acarolinebcosta.serverest.cart;

import java.util.ArrayList;
import java.util.List;

public final class CartBuilder {

    private final List<CartItemRequest> products = new ArrayList<>();

    public CartBuilder addItem(String productId, int quantity) {
        products.add(new CartItemRequest(productId, quantity));
        return this;
    }

    public CartRequest build() {
        return new CartRequest(List.copyOf(products));
    }
}