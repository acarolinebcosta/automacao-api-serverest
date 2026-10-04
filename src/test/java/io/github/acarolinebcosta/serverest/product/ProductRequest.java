package io.github.acarolinebcosta.serverest.product;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductRequest(@JsonProperty("nome") String name,
                             @JsonProperty("preco") Integer price,
                             @JsonProperty("descricao") String description,
                             @JsonProperty("quantidade") Integer quantity) {
}
