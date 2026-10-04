package io.github.acarolinebcosta.serverest.evidence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.acarolinebcosta.serverest.auth.LoginRequest;
import io.github.acarolinebcosta.serverest.auth.LoginResponse;
import io.github.acarolinebcosta.serverest.user.UserRequest;
import io.github.acarolinebcosta.serverest.user.UserResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression tests for {@link EvidenceSanitizer}.
 *
 * Verifies that sensitive data such as emails, passwords, Bearer tokens,
 * JWTs and authorization values are not exposed while useful diagnostic
 * and business information remains available.
 */
class EvidenceSanitizerTest {

    private static final String REDACTED = "[REDACTED]";

    private static final String EMAIL = "security.fixture@example.test";
    private static final String PASSWORD = "SensitiveFixture!2026";
    private static final String OPAQUE_TOKEN =
            "opaque-credential-without-jwt-format";

    private static final String JWT =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
                    + "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IlRlc3QgVXNlciJ9"
                    + ".abcdefghijklmnopqrstuvwxyz0123456789ABCDEFG";

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void redactsCredentialsInNestedObjectsAndArraysWithoutRemovingBusinessData()
            throws Exception {

        Map<String, Object> evidence = Map.of(
                "request", Map.of(
                        "Email", EMAIL,
                        "PASSWORD", PASSWORD,
                        "quantity", 3
                ),
                "sessions", List.of(
                        Map.of("Authorization", "Bearer " + JWT),
                        Map.of("Senha", PASSWORD),
                        Map.of("jWt", JWT)
                ),
                "message", "Business validation failed"
        );

        String sanitized =
                new EvidenceSanitizer().sanitize(evidence);

        JsonNode result = mapper.readTree(sanitized);

        assertThat(sanitized)
                .doesNotContain(EMAIL, PASSWORD, JWT);

        assertThat(result.at("/request/Email").asText())
                .isEqualTo(REDACTED);

        assertThat(result.at("/request/PASSWORD").asText())
                .isEqualTo(REDACTED);

        assertThat(result.at("/sessions/0/Authorization").asText())
                .isEqualTo(REDACTED);

        assertThat(result.at("/sessions/1/Senha").asText())
                .isEqualTo(REDACTED);

        assertThat(result.at("/sessions/2/jWt").asText())
                .isEqualTo(REDACTED);

        assertThat(result.at("/request/quantity").asInt())
                .isEqualTo(3);

        assertThat(result.path("message").asText())
                .isEqualTo("Business validation failed");
    }

    @Test
    void redactsPreviouslySubmittedSecretsWhenResponseReflectsThemWithoutFieldLabels()
            throws Exception {

        EvidenceSanitizer sanitizer = new EvidenceSanitizer();

        sanitizer.sanitize(
                new LoginRequest(EMAIL, PASSWORD)
        );

        sanitizer.sanitize(
                Map.of("authorization", OPAQUE_TOKEN)
        );

        String sanitized = sanitizer.sanitize(
                Map.of(
                        "message",
                        "Rejected supplied value "
                                + PASSWORD
                                + " and "
                                + OPAQUE_TOKEN
                                + ". Nothing updated.",
                        "operation",
                        "POST /usuarios"
                )
        );

        assertThat(sanitized)
                .doesNotContain(PASSWORD, OPAQUE_TOKEN);

        JsonNode result = mapper.readTree(sanitized);

        assertThat(result.path("message").asText())
                .isEqualTo(
                        "Rejected supplied value "
                                + REDACTED
                                + " and "
                                + REDACTED
                                + ". Nothing updated."
                );

        assertThat(result.path("operation").asText())
                .isEqualTo("POST /usuarios");
    }

    @Test
    void redactsFreeTextCredentialsWhilePreservingExceptionAndVersionDiagnostics() {
        String text =
                "java.lang.IllegalStateException in Jackson 2.22.2: contact "
                        + EMAIL
                        + "; header Bearer "
                        + OPAQUE_TOKEN
                        + "; credential "
                        + JWT
                        + "; senha='"
                        + PASSWORD
                        + "'";

        String sanitized =
                new EvidenceSanitizer().sanitizeText(text);

        assertThat(sanitized)
                .doesNotContain(
                        EMAIL,
                        PASSWORD,
                        OPAQUE_TOKEN,
                        JWT
                );

        assertThat(sanitized)
                .contains(
                        "java.lang.IllegalStateException",
                        "Jackson 2.22.2",
                        "Bearer " + REDACTED
                );
    }

    @Test
    void omitsNonJsonResponseWithoutPublishingItsReflectedCredentials() {
        String body =
                "<html><h1>Proxy error</h1><p>"
                        + EMAIL
                        + " "
                        + PASSWORD
                        + " "
                        + JWT
                        + "</p></html>";

        String sanitized =
                new EvidenceSanitizer().sanitize(body);

        assertThat(sanitized)
                .isNotBlank()
                .contains("Evidência não-JSON omitida");

        assertThat(sanitized)
                .doesNotContain(
                        EMAIL,
                        PASSWORD,
                        JWT,
                        "<html>"
                );
    }

    @Test
    void omitsMalformedJsonWithoutPublishingCredentialsFromTheParserError() {
        String body =
                "{\"password\":\""
                        + PASSWORD
                        + "\",\"authorization\":\"Bearer "
                        + JWT
                        + "\",";

        String sanitized =
                new EvidenceSanitizer().sanitize(body);

        assertThat(sanitized)
                .isNotBlank()
                .contains("Evidência não-JSON omitida");

        assertThat(sanitized)
                .doesNotContain(
                        PASSWORD,
                        JWT,
                        "Bearer"
                );
    }

    @Test
    void leavesTheOriginalRequestAndItsExternalJsonContractUnchanged()
            throws Exception {

        UserRequest request = new UserRequest(
                "Security fixture",
                EMAIL,
                PASSWORD,
                "true"
        );

        String originalBody =
                mapper.writeValueAsString(request);

        String sanitized =
                new EvidenceSanitizer().sanitize(request);

        assertThat(sanitized)
                .doesNotContain(EMAIL, PASSWORD);

        assertThat(mapper.writeValueAsString(request))
                .isEqualTo(originalBody);

        JsonNode original =
                mapper.readTree(originalBody);

        assertThat(original.path("email").asText())
                .isEqualTo(EMAIL);

        assertThat(original.path("password").asText())
                .isEqualTo(PASSWORD);

        assertThat(original.path("nome").asText())
                .isEqualTo("Security fixture");

        assertThat(original.path("administrador").asText())
                .isEqualTo("true");
    }

    @Test
    void keepsCredentialValuesOutOfSensitiveDtoStringRepresentations() {
        List<Object> values = List.of(
                new LoginRequest(
                        EMAIL,
                        PASSWORD
                ),
                new LoginResponse(
                        "Login realizado com sucesso",
                        "Bearer " + JWT
                ),
                new UserRequest(
                        "Security fixture",
                        EMAIL,
                        PASSWORD,
                        "true"
                ),
                new UserResponse(
                        "Security fixture",
                        EMAIL,
                        "true",
                        "abcdefghijklmnop"
                )
        );

        assertThat(values)
                .allSatisfy(value ->
                        assertThat(value.toString())
                                .contains(REDACTED)
                                .doesNotContain(
                                        EMAIL,
                                        PASSWORD,
                                        JWT
                                )
                );
    }
}