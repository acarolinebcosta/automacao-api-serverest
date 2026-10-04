package io.github.acarolinebcosta.serverest.cart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CartListResponse(
        @JsonProperty("quantidade") Integer quantity,
        @JsonProperty("carrinhos") List<CartResponse> carts
) {
    public CartListResponse {
        carts = carts == null ? null : List.copyOf(carts);
    }
}
