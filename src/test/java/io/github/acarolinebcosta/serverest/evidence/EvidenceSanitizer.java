package io.github.acarolinebcosta.serverest.evidence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Sanitizes sensitive data before test evidence is published.
 *
 * Emails, passwords, authentication tokens and authorization values are
 * removed from structured JSON and plain text evidence.
 */
public final class EvidenceSanitizer {

    private static final String REDACTED = "[REDACTED]";

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "email",
            "password",
            "senha",
            "authorization",
            "token",
            "jwt",
            "access_token",
            "refresh_token",
            "id_token"
    );

    private static final Pattern BEARER_TOKEN_PATTERN =
            Pattern.compile("(?i)(?:Bearer\\s+)[^\\s\\\"',;<>]+");

    private static final Pattern JWT_PATTERN =
            Pattern.compile(
                    "eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+"
            );

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}"
            );

    private static final Pattern SECRET_KEY_VALUE_PATTERN =
            Pattern.compile(
                    "(?i)((?:password|senha|authorization|token|jwt)\\s*[:=]\\s*)"
                            + "(?:\\\"[^\\\"]*\\\"|'[^']*'|[^\\s,;]+)"
            );

    private final ObjectMapper mapper = new ObjectMapper();
    private final Set<String> secrets = new HashSet<>();

    public String sanitize(Object value) {
        if (value == null) {
            return "";
        }

        try {
            JsonNode root = value instanceof String json
                    ? mapper.readTree(json)
                    : mapper.valueToTree(value);

            rememberSecrets(root);
            root = redact(root);

            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(root);
        } catch (JsonProcessingException | IllegalArgumentException failure) {
            return "[Evidência não-JSON omitida: "
                    + failure.getClass().getSimpleName()
                    + "]";
        }
    }

    public String sanitizeText(String value) {
        if (value == null) {
            return "";
        }

        String safe = value;

        for (String secret : secrets.stream()
                .sorted(
                        Comparator.comparingInt(String::length)
                                .reversed()
                )
                .toList()) {
            safe = safe.replace(secret, REDACTED);
        }

        safe = BEARER_TOKEN_PATTERN.matcher(safe)
                .replaceAll("Bearer " + REDACTED);

        safe = JWT_PATTERN.matcher(safe)
                .replaceAll(REDACTED);

        safe = EMAIL_PATTERN.matcher(safe)
                .replaceAll(REDACTED);

        safe = SECRET_KEY_VALUE_PATTERN.matcher(safe)
                .replaceAll("$1" + REDACTED);

        return safe;
    }

    private void rememberSecrets(JsonNode node) {
        if (node == null) {
            return;
        }

        if (node.isObject()) {
            node.properties().forEach(field -> {
                boolean sensitive = SENSITIVE_FIELDS.contains(
                        field.getKey().toLowerCase(Locale.ROOT)
                );

                if (sensitive && field.getValue().isTextual()) {
                    String secret = field.getValue().asText();

                    if (!secret.isBlank() && !secret.equals(REDACTED)) {
                        secrets.add(secret);
                    }
                } else {
                    rememberSecrets(field.getValue());
                }
            });

            return;
        }

        if (node.isArray()) {
            node.forEach(this::rememberSecrets);
        }
    }

    private JsonNode redact(JsonNode node) {
        if (node == null) {
            return null;
        }

        if (node.isTextual()) {
            return TextNode.valueOf(
                    sanitizeText(node.asText())
            );
        }

        if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;

            List<String> names = new ArrayList<>();
            object.fieldNames().forEachRemaining(names::add);

            for (String name : names) {
                boolean sensitive = SENSITIVE_FIELDS.contains(
                        name.toLowerCase(Locale.ROOT)
                );

                object.set(
                        name,
                        sensitive
                                ? TextNode.valueOf(REDACTED)
                                : redact(object.get(name))
                );
            }

            return object;
        }

        if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;

            for (int i = 0; i < array.size(); i++) {
                array.set(
                        i,
                        redact(array.get(i))
                );
            }
        }

        return node;
    }
}