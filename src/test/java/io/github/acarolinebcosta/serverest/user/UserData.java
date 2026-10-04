package io.github.acarolinebcosta.serverest.user;

import io.github.acarolinebcosta.serverest.testdata.DataGenerator;

public final class UserData {

    private UserData() {
    }

    public static UserRequest validAdmin() {
        return new UserBuilder()
                .withName(DataGenerator.uniqueUserName())
                .withEmail(DataGenerator.uniqueEmail())
                .withPassword("QA!Automation#2026")
                .withAdministrator("true")
                .build();
    }
}
