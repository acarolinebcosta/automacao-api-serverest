package io.github.acarolinebcosta.serverest.cart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CartResponse(
        @JsonProperty("produtos") List<CartItemResponse> products,
        @JsonProperty("precoTotal") Long totalPrice,
        @JsonProperty("quantidadeTotal") Integer totalQuantity,
        @JsonProperty("idUsuario") String userId,
        @JsonProperty("_id") String id
) {
    public CartResponse {
        products = products == null ? null : List.copyOf(products);
    }
}
