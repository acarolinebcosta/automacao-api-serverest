package io.github.acarolinebcosta.serverest.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductResponse(@JsonProperty("nome") String name,
                              @JsonProperty("preco") Integer price,
                              @JsonProperty("descricao") String description,
                              @JsonProperty("quantidade") Integer quantity,
                              @JsonProperty("_id") String id) {
}
