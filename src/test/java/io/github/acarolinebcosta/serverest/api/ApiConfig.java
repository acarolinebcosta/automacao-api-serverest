package io.github.acarolinebcosta.serverest.api;

import io.github.acarolinebcosta.serverest.evidence.SafeEvidenceFilter;
import io.github.acarolinebcosta.serverest.evidence.EvidenceSanitizer;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final String baseUrl;
    private final RestAssuredConfig configuration;
    private final EvidenceSanitizer sanitizer;

    public ApiConfig(EvidenceSanitizer sanitizer) {
        this.sanitizer = sanitizer;
        baseUrl = System.getProperty("baseUrl", "https://serverest.dev");
        URI uri = URI.create(baseUrl);
        if (uri.getHost() == null || !("https".equals(uri.getScheme()) || "http".equals(uri.getScheme()))) {
            throw new IllegalArgumentException("baseUrl must be a valid HTTP or HTTPS URL");
        }
        int timeout = Integer.parseInt(System.getProperty("api.timeout.ms", "15000"));
        if (timeout <= 0) throw new IllegalArgumentException("api.timeout.ms must be positive");
        configuration = RestAssuredConfig.config()
                .objectMapperConfig(objectMapperConfig().defaultObjectMapperType(ObjectMapperType.JACKSON_2)
                        .jackson2ObjectMapperFactory((type, charset) -> new ObjectMapper()))
                .httpClient(httpClientConfig()
                        .setParam("http.connection.timeout", timeout)
                        .setParam("http.socket.timeout", timeout));
    }

    public RequestSpecification request() {
        return given().spec(new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(configuration)
                .addFilter(new SafeEvidenceFilter(sanitizer))
                .build());
    }

    public RequestSpecification authenticated(String token) {
        if (token == null || token.isBlank()) throw new IllegalArgumentException("Authorization token is required");
        return request().header("Authorization", token);
    }
}
