package io.github.acarolinebcosta.serverest.validation;

import io.github.acarolinebcosta.serverest.evidence.EvidenceSanitizer;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Validates API response contracts against JSON schemas stored in
 * {@code src/test/resources/schemas}.
 *
 * Each validation runs as an Allure step. On failure, the response body
 * and failure reason are sanitized before being attached to the report
 * to prevent credentials from being exposed in test evidence.
 */
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
        Allure.step("Validar contrato JSON: " + schema, () -> {
            try {
                assertThat(
                        "Contrato JSON: " + schema,
                        response.asString(),
                        matchesJsonSchemaInClasspath(
                                "schemas/" + schema + ".schema.json"
                        )
                );
            } catch (AssertionError | RuntimeException failure) {
                String safeBody = sanitizer.sanitize(
                        response.asString()
                );

                Allure.addAttachment(
                        "Falha de contrato - " + schema,
                        "application/json",
                        safeBody,
                        ".json"
                );

                String reason = sanitizer.sanitize(Map.of(
                        "tipo", failure.getClass().getSimpleName(),
                        "mensagem", String.valueOf(failure.getMessage())
                ));

                Allure.addAttachment(
                        "Motivo da falha de contrato - " + schema,
                        "application/json",
                        reason,
                        ".json"
                );

                // The original matcher failure may contain the raw response body and must not be published.
                throw new AssertionError(
                        "Contrato JSON inválido: "
                                + schema
                                + "; ver evidência sanitizada"
                );
            }
        });
    }
}