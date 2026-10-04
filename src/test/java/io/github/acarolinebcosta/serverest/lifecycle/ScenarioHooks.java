package io.github.acarolinebcosta.serverest.lifecycle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.acarolinebcosta.serverest.evidence.EvidenceSanitizer;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Runs after each scenario to clean created resources and report cleanup
 * failures without masking the primary test failure.
 *
 * Cleanup failures are accumulated instead of stopping at the first one.
 * If the scenario passed but cleanup failed, an aggregated AssertionError
 * is thrown with the sanitized failures added as suppressed exceptions.
 *
 * All published failure messages are sanitized to prevent credentials
 * from being exposed in test evidence.
 */
@RequiredArgsConstructor
public final class ScenarioHooks {

    private final CleanupService cleanupService;
    private final EvidenceSanitizer sanitizer;

    @After
    public void cleanupScenarioResources(Scenario scenario) {
        List<Throwable> failures = cleanupService.cleanup();

        if (failures.isEmpty()) {
            return;
        }

        String details = sanitizer.sanitize(
                failures.stream()
                        .map(failure -> new CleanupFailure(
                                failure.getClass().getSimpleName(),
                                failure.getMessage()
                        ))
                        .toList()
        );

        Allure.addAttachment(
                "Falhas de limpeza",
                "application/json",
                details,
                ".json"
        );

        scenario.attach(
                details,
                "application/json",
                "Falhas de limpeza"
        );

        if (!scenario.isFailed()) {
            AssertionError cleanupFailure = new AssertionError(
                    "A limpeza falhou em "
                            + failures.size()
                            + " operação(ões); consulte a evidência sanitizada"
            );

            for (Throwable failure : failures) {
                // Suppressed errors are published by the runner and must not expose raw credentials.
                AssertionError safeFailure = new AssertionError(
                        sanitizer.sanitizeText(failure.getMessage())
                );

                safeFailure.setStackTrace(failure.getStackTrace());
                cleanupFailure.addSuppressed(safeFailure);
            }

            throw cleanupFailure;
        }
    }

    private record CleanupFailure(
            @JsonProperty("tipo") String type,
            @JsonProperty("mensagem") String message
    ) {
    }
}