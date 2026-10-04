package io.github.acarolinebcosta.serverest.product;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.api.CreateResponse;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.validation.ContractAssertions;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public final class ProductService {

    private final ScenarioContext context;
    private final ProductClient productClient;
    private final ContractAssertions contractAssertions;

    @Step("CT03-CT04 - Create product and confirm initial stock")
    public CreatedProduct createProduct(String alias, int price, int stock) {
        assertThat(alias).as("Product alias").isNotBlank();
        assertThat(context.getProducts()).extracting(CreatedProduct::alias)
                .as("Product alias must be unique within the scenario").doesNotContain(alias);

        ProductRequest request = ProductData.valid(price, stock);
        Response response = productClient.create(context.getToken(), request);
        ResponseAssertions.assertStatus(response, 201, "Create product");
        CreateResponse createdProduct = response.as(CreateResponse.class);
        assertThat(createdProduct.id()).as("Created product ID").isNotBlank();

        CreatedProduct product = new CreatedProduct(alias, createdProduct.id(), request, request.quantity());
        context.addProduct(product);
        ResponseAssertions.assertMessage(response, 201, ApiMessages.CREATE_SUCCESS, "Create product");
        validatePersistence(product);
        return product;
    }

    @Step("Find product and validate response")
    public ProductResponse findProduct(CreatedProduct product) {
        Response response = productClient.findById(product.id());
        ResponseAssertions.assertStatus(response, 200, "Find product");
        contractAssertions.validateProduct(response);
        ProductResponse persistedProduct = response.as(ProductResponse.class);
        assertThat(persistedProduct.id()).as("Returned product ID").isEqualTo(product.id());
        return persistedProduct;
    }

    @Step("Confirm product does not exist")
    public void assertNonexistent(String id) {
        ResponseAssertions.assertMessage(productClient.findById(id), 400,
                ApiMessages.PRODUCT_NOT_FOUND, "Confirm nonexistent product");
    }

    @Step("Validate product stock")
    public void assertStock(CreatedProduct product, int expectedStock) {
        assertThat(findProduct(product).quantity())
                .as("Product stock: productId=%s, initial=%d, expected=%d",
                        product.id(), product.initialStock(), expectedStock)
                .isEqualTo(expectedStock);
    }

    public CreatedProduct getByAlias(String alias) {
        return context.getProducts().stream()
                .filter(product -> product.alias().equals(alias))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Product was not prepared in the scenario: " + alias));
    }

    private void validatePersistence(CreatedProduct product) {
        ProductResponse persistedProduct = findProduct(product);
        ProductRequest expectedProduct = product.request();
        assertThat(persistedProduct.name()).as("Persisted product name").isEqualTo(expectedProduct.name());
        assertThat(persistedProduct.description()).as("Persisted product description")
                .isEqualTo(expectedProduct.description());
        assertThat(persistedProduct.price()).as("Persisted product price").isEqualTo(expectedProduct.price());
        assertThat(persistedProduct.quantity()).as("Persisted initial product stock").isEqualTo(product.initialStock());
    }
}
