package io.github.acarolinebcosta.serverest.auth;

public record LoginRequest(String email, String password) {

    @Override
    public String toString() {
        return "LoginRequest[email=[REDACTED], password=[REDACTED]]";
    }
}
