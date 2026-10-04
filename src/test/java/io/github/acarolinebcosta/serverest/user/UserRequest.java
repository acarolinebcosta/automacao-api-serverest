package io.github.acarolinebcosta.serverest.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserRequest(@JsonProperty("nome") String name,
                          String email,
                          String password,
                          @JsonProperty("administrador") String administrator) {

    @Override
    public String toString() {
        return "UserRequest[name=" + name + ", email=[REDACTED], password=[REDACTED], administrator="
                + administrator + "]";
    }
}
