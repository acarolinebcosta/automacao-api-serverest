package io.github.acarolinebcosta.serverest.cart;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CartRequest(@JsonProperty("produtos") List<CartItemRequest> products) {
    public CartRequest {
        products = List.copyOf(products);
    }
}
