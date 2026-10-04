package io.github.acarolinebcosta.serverest.validation;

import io.github.acarolinebcosta.serverest.evidence.EvidenceSanitizer;
import io.qameta.allure.Allure;
import io.restassured.response.Response;

import java.util.Map;
import lombok.RequiredArgsConstructor;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

@RequiredArgsConstructor
public final class ContractAssertions {
    private final EvidenceSanitizer sanitizer;

    public void validateLogin(Response response) {
        validate(response, "login");
    }

    public void validateProduct(Response response) {
        validate(response, "produto");
    }

    public void validateCarts(Response response) {
        validate(response, "carrinhos");
    }

    private void validate(Response response, String schema) {
        Allure.step("Validate JSON contract: " + schema, () -> {
            try {
                assertThat("JSON contract: " + schema, response.asString(),
                        matchesJsonSchemaInClasspath("schemas/" + schema + ".schema.json"));
            } catch (AssertionError | RuntimeException failure) {
                String safeBody = sanitizer.sanitize(response.asString());
                Allure.addAttachment("Contract failure - " + schema, "application/json", safeBody, ".json");
                String reason = sanitizer.sanitize(Map.of(
                        "type", failure.getClass().getSimpleName(),
                        "message", String.valueOf(failure.getMessage())));
                Allure.addAttachment("Contract failure reason - " + schema, "application/json", reason, ".json");
                throw new AssertionError("JSON contract failed: " + schema + "; see sanitized evidence");
            }
        });
    }
}
