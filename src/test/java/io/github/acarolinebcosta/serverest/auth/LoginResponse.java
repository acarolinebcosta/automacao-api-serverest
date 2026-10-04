package io.github.acarolinebcosta.serverest.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginResponse(String message, String authorization) {

    @Override
    public String toString() {
        return "LoginResponse[message=" + message + ", authorization=[REDACTED]]";
    }
}
