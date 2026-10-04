package io.github.acarolinebcosta.serverest.testdata;

import java.util.UUID;

public final class DataGenerator {

    private DataGenerator() {
    }

    public static String uniqueUserName() {
        return "QA User " + uniqueSuffix();
    }

    public static String uniqueEmail() {
        return "qa." + uniqueSuffix() + "@example.com";
    }

    public static String uniqueProductName() {
        return "QA Product " + uniqueSuffix();
    }

    public static String nonexistentProductId() {
        return uniqueSuffix().substring(0, 16);
    }

    private static String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
