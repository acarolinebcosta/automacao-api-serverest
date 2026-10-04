package io.github.acarolinebcosta.serverest.cart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CartItemResponse(
        @JsonProperty("idProduto") String productId,
        @JsonProperty("quantidade") Integer quantity,
        @JsonProperty("precoUnitario") Long unitPrice
) {
}
