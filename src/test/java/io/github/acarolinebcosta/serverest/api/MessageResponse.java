package io.github.acarolinebcosta.serverest.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MessageResponse(String message) {
}
