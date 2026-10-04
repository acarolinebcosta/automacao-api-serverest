package io.github.acarolinebcosta.serverest.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateResponse(String message, @JsonProperty("_id") String id) {
}