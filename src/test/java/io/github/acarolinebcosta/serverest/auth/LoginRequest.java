package io.github.acarolinebcosta.serverest.auth;

public record LoginRequest(String email, String password) {

    private static final String REDACTED = "[REDACTED]";

    @Override
    public String toString() {
        return "LoginRequest[email=" + REDACTED
                + ", password=" + REDACTED + "]";
    }
}