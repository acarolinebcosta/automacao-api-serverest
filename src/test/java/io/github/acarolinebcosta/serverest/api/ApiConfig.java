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

public final class ApiConfig {

    private static final String DEFAULT_BASE_URL = "https://serverest.dev";
    private static final String DEFAULT_TIMEOUT_MS = "15000";

    private static final String INVALID_BASE_URL_MESSAGE =
            "baseUrl deve ser uma URL HTTP ou HTTPS válida";

    private static final String INVALID_TIMEOUT_MESSAGE =
            "api.timeout.ms deve ser um número inteiro positivo";

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
                        .setContentType("application/json; charset=UTF-8")
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
            throw new IllegalArgumentException(INVALID_BASE_URL_MESSAGE);
        }

        boolean validScheme =
                "https".equalsIgnoreCase(uri.getScheme())
                        || "http".equalsIgnoreCase(uri.getScheme());

        if (uri.getHost() == null || !validScheme) {
            throw new IllegalArgumentException(INVALID_BASE_URL_MESSAGE);
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
            throw new IllegalArgumentException(INVALID_TIMEOUT_MESSAGE);
        }

        if (timeout <= 0) {
            throw new IllegalArgumentException(INVALID_TIMEOUT_MESSAGE);
        }

        return timeout;
    }
}