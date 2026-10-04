package io.github.acarolinebcosta.serverest.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnvironmentConfig {

    private static final String TEST_PASSWORD = "SERVEREST_TEST_PASSWORD";

    public String testPassword() {
        String password = System.getenv(TEST_PASSWORD);

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "A variável de ambiente SERVEREST_TEST_PASSWORD é obrigatória"
            );
        }

        return password;
    }
}