package io.github.acarolinebcosta.serverest.user;

import io.github.acarolinebcosta.serverest.config.EnvironmentConfig;
import io.github.acarolinebcosta.serverest.testdata.DataGenerator;

public final class UserData {

    public static UserRequest validAdmin() {
        return new UserBuilder()
                .withName(DataGenerator.uniqueUserName())
                .withEmail(DataGenerator.uniqueEmail())
                .withPassword(EnvironmentConfig.testPassword())
                .withAdministrator("true")
                .build();
    }

    private UserData() {
        throw new UnsupportedOperationException("Utility class");
    }
}