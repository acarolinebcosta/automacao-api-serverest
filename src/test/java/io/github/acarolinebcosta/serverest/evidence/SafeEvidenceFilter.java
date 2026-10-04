package io.github.acarolinebcosta.serverest.evidence;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.RequiredArgsConstructor;

/**
 * Attaches each scenario HTTP request and response to the Allure report
 * after sanitizing their contents through {@link EvidenceSanitizer}.
 *
 * The filter provides diagnostic evidence without exposing credentials,
 * tokens or other sensitive data.
 */
@RequiredArgsConstructor
public final class SafeEvidenceFilter implements Filter {

    private final EvidenceSanitizer sanitizer;

    @Override
    public Response filter(
            FilterableRequestSpecification request,
            FilterableResponseSpecification response,
            FilterContext context
    ) {
        String route = sanitizer.sanitizeText(
                request.getMethod() + " " + request.getURI()
        );

        if (request.getBody() != null) {
            Allure.addAttachment(
                    route + " - requisição",
                    "application/json",
                    sanitizer.sanitize(request.getBody()),
                    ".json"
            );
        }

        Response result = context.next(request, response);

        Allure.addAttachment(
                route + " - HTTP " + result.statusCode(),
                "application/json",
                sanitizer.sanitize(result.asString()),
                ".json"
        );

        return result;
    }
}