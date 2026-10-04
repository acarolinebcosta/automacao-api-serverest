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
import java.util.Locale;
import java.util.Set;

public final class EvidenceSanitizer {
    private static final String REDACTED = "[REDACTED]";
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "email", "password", "senha", "authorization", "token", "jwt",
            "access_token", "refresh_token", "id_token");

    private final ObjectMapper mapper = new ObjectMapper();
    private final Set<String> secrets = new HashSet<>();

    public String sanitize(Object value) {
        if (value == null) return "";
        try {
            JsonNode root = value instanceof String json ? mapper.readTree(json) : mapper.valueToTree(value);
            rememberSecrets(root);
            root = redact(root);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (JsonProcessingException | IllegalArgumentException failure) {
            return "[Non-JSON evidence omitted: " + failure.getClass().getSimpleName() + "]";
        }
    }

    public String sanitizeText(String value) {
        if (value == null) return "";
        String safe = value;
        for (String secret : secrets.stream().sorted(Comparator.comparingInt(String::length).reversed()).toList()) {
            safe = safe.replace(secret, REDACTED);
        }
        return safe.replaceAll("(?i)(?:Bearer\\s+)[^\\s\\\"',;<>]+", "Bearer " + REDACTED)
                .replaceAll("eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+", REDACTED)
                .replaceAll("[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}", REDACTED)
                .replaceAll("(?i)((?:password|senha|authorization|token|jwt)\\s*[:=]\\s*)(?:\\\"[^\\\"]*\\\"|'[^']*'|[^\\s,;]+)", "$1" + REDACTED);
    }

    private void rememberSecrets(JsonNode node) {
        if (node == null) return;
        if (node.isObject()) {
            node.properties().forEach(field -> {
                if (SENSITIVE_FIELDS.contains(field.getKey().toLowerCase(Locale.ROOT)) && field.getValue().isTextual()) {
                    String secret = field.getValue().asText();
                    if (!secret.isBlank() && !secret.equals(REDACTED)) secrets.add(secret);
                } else {
                    rememberSecrets(field.getValue());
                }
            });
        } else if (node.isArray()) {
            node.forEach(this::rememberSecrets);
        }
    }

    private JsonNode redact(JsonNode node) {
        if (node == null) return null;
        if (node.isTextual()) return TextNode.valueOf(sanitizeText(node.asText()));
        if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            var names = new ArrayList<String>();
            object.fieldNames().forEachRemaining(names::add);
            for (String name : names) {
                object.set(name, SENSITIVE_FIELDS.contains(name.toLowerCase(Locale.ROOT))
                        ? TextNode.valueOf(REDACTED) : redact(object.get(name)));
            }
        } else if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;
            for (int i = 0; i < array.size(); i++) array.set(i, redact(array.get(i)));
        }
        return node;
    }
}
