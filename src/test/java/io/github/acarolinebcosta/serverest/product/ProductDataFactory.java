package io.github.acarolinebcosta.serverest.product;

import io.github.acarolinebcosta.serverest.api.CreateResponse;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.restassured.response.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ProductDataFactory {

    private static final String AUXILIARY_ALIAS_PREFIX = "aux-";

    @NonNull
    private final ProductClient productClient;

    @NonNull
    private final ScenarioContext context;

    public String createAndReturnName() {
        ProductRequest request = ProductData.valid(100, 10);
        Response response = productClient.create(context.getToken(), request);

        if (response.statusCode() == 201) {
            String createdId = response.as(CreateResponse.class).id();
            String alias = AUXILIARY_ALIAS_PREFIX + createdId;

            context.addProduct(new CreatedProduct(
                    alias,
                    createdId,
                    request
            ));
        }

        return request.name();
    }
}