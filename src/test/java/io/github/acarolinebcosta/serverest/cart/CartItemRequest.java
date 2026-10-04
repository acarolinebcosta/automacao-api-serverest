package io.github.acarolinebcosta.serverest.cart;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CartItemRequest(
        @JsonProperty("idProduto") String productId,
        @JsonProperty("quantidade") Integer quantity
) {
}
