package io.github.acarolinebcosta.serverest.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserResponse(@JsonProperty("nome") String name,
                           String email,
                           @JsonProperty("administrador") String administrator,
                           @JsonProperty("_id") String id) {

    @Override
    public String toString() {
        return "UserResponse[name=" + name + ", email=[REDACTED], administrator="
                + administrator + ", id=" + id + "]";
    }
}
