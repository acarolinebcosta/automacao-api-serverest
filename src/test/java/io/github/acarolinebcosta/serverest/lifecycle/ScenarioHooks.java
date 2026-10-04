package io.github.acarolinebcosta.serverest.lifecycle;

import io.github.acarolinebcosta.serverest.evidence.EvidenceSanitizer;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ScenarioHooks {
    private final CleanupService cleanupService;
    private final EvidenceSanitizer sanitizer;

    @After
    public void cleanupScenarioResources(Scenario scenario) {
        var failures = cleanupService.cleanup();
        if (failures.isEmpty()) return;

        String details = sanitizer.sanitize(failures.stream()
                .map(failure -> new CleanupFailure(failure.getClass().getSimpleName(), failure.getMessage()))
                .toList());
        Allure.addAttachment("Cleanup failures", "application/json", details, ".json");
        scenario.attach(details, "application/json", "Cleanup failures");

        if (!scenario.isFailed()) {
            var cleanupFailure = new AssertionError(
                    "Cleanup failed in " + failures.size() + " operation(s); see sanitized evidence");
            for (Throwable failure : failures) {
                // Suppressed errors are published by the runner and must never expose raw credentials.
                var safeFailure = new AssertionError(sanitizer.sanitizeText(failure.getMessage()));
                safeFailure.setStackTrace(failure.getStackTrace());
                cleanupFailure.addSuppressed(safeFailure);
            }
            throw cleanupFailure;
        }
    }

    private record CleanupFailure(String type, String message) { }
}
