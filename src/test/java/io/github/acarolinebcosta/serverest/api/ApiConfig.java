package io.github.acarolinebcosta.serverest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.acarolinebcosta.serverest.evidence.EvidenceSanitizer;
import io.github.acarolinebcosta.serverest.evidence.SafeEvidenceFilter;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.specification.RequestSpecification;

import java.net.URI;

import static io.restassured.RestAssured.given;
import static io.restassured.config.HttpClientConfig.httpClientConfig;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

/**
 * Central REST Assured configuration for the test suite.
 *
 * Reads the base URL and timeout from system properties, validates their
 * values and builds the {@link RequestSpecification} used by API clients.
 *
 * Each instance keeps its own configuration and shared
 * {@link EvidenceSanitizer}, allowing HTTP evidence to be sanitized across
 * requests within the same scenario.
 */
public final class ApiConfig {

    private static final String DEFAULT_BASE_URL = "https://serverest.dev";
    private static final String DEFAULT_TIMEOUT_MS = "15000";

    private final String baseUrl;
    private final RestAssuredConfig configuration;
    private final EvidenceSanitizer sanitizer;

    public ApiConfig(EvidenceSanitizer sanitizer) {
        this.sanitizer = sanitizer;
        this.baseUrl = resolveBaseUrl();
        this.configuration = buildConfiguration(resolveTimeout());
    }

    public RequestSpecification request() {
        return given().spec(
                new RequestSpecBuilder()
                        .setBaseUri(baseUrl)
                        .setContentType(ContentType.JSON)
                        .setAccept(ContentType.JSON)
                        .setConfig(configuration)
                        .addFilter(new SafeEvidenceFilter(sanitizer))
                        .build()
        );
    }

    public RequestSpecification authenticated(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "O token de autorização é obrigatório"
            );
        }

        return request()
                .header("Authorization", token);
    }

    private static String resolveBaseUrl() {
        String configuredBaseUrl =
                System.getProperty("baseUrl", DEFAULT_BASE_URL);

        URI uri;

        try {
            uri = URI.create(configuredBaseUrl);
        } catch (IllegalArgumentException failure) {
            throw new IllegalArgumentException(
                    "baseUrl deve ser uma URL HTTP ou HTTPS válida"
            );
        }

        boolean validScheme =
                "https".equalsIgnoreCase(uri.getScheme())
                        || "http".equalsIgnoreCase(uri.getScheme());

        if (uri.getHost() == null || !validScheme) {
            throw new IllegalArgumentException(
                    "baseUrl deve ser uma URL HTTP ou HTTPS válida"
            );
        }

        return configuredBaseUrl;
    }

    private static RestAssuredConfig buildConfiguration(int timeout) {
        return RestAssuredConfig.config()
                .objectMapperConfig(
                        objectMapperConfig()
                                .defaultObjectMapperType(
                                        ObjectMapperType.JACKSON_2
                                )
                                .jackson2ObjectMapperFactory(
                                        (type, charset) -> new ObjectMapper()
                                )
                )
                .httpClient(
                        httpClientConfig()
                                .setParam(
                                        "http.connection.timeout",
                                        timeout
                                )
                                .setParam(
                                        "http.socket.timeout",
                                        timeout
                                )
                );
    }

    private static int resolveTimeout() {
        String configuredTimeout =
                System.getProperty(
                        "api.timeout.ms",
                        DEFAULT_TIMEOUT_MS
                );

        int timeout;

        try {
            timeout = Integer.parseInt(configuredTimeout);
        } catch (NumberFormatException failure) {
            throw new IllegalArgumentException(
                    "api.timeout.ms deve ser um número inteiro positivo"
            );
        }

        if (timeout <= 0) {
            throw new IllegalArgumentException(
                    "api.timeout.ms deve ser um número inteiro positivo"
            );
        }

        return timeout;
    }
}