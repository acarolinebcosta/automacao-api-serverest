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

    @Step("CT03-CT04 - Criar produto e confirmar estoque inicial")
    public CreatedProduct createProduct(String alias, int price, int stock) {
        assertThat(alias)
                .as("Alias do produto")
                .isNotBlank();

        assertThat(context.getProducts())
                .extracting(CreatedProduct::alias)
                .as("O alias do produto deve ser único no cenário")
                .doesNotContain(alias);

        ProductRequest request = ProductData.valid(price, stock);
        Response response = productClient.create(context.getToken(), request);

        ResponseAssertions.assertStatus(
                response,
                201,
                "Criar produto"
        );

        CreateResponse createdProduct = response.as(CreateResponse.class);

        assertThat(createdProduct.id())
                .as("ID do produto criado")
                .isNotBlank();

        CreatedProduct product = new CreatedProduct(
                alias,
                createdProduct.id(),
                request,
                request.quantity()
        );

        context.addProduct(product);

        ResponseAssertions.assertMessage(
                response,
                201,
                ApiMessages.CREATE_SUCCESS,
                "Criar produto"
        );

        validatePersistence(product);

        return product;
    }

    @Step("Buscar produto e validar resposta")
    public ProductResponse findProduct(CreatedProduct product) {
        Response response = productClient.findById(product.id());

        ResponseAssertions.assertStatus(
                response,
                200,
                "Buscar produto"
        );

        contractAssertions.validateProduct(response);

        ProductResponse persistedProduct = response.as(ProductResponse.class);

        assertThat(persistedProduct.id())
                .as("ID do produto retornado")
                .isEqualTo(product.id());

        return persistedProduct;
    }

    @Step("Confirmar que o produto não existe")
    public void assertNonexistent(String id) {
        ResponseAssertions.assertMessage(
                productClient.findById(id),
                400,
                ApiMessages.PRODUCT_NOT_FOUND,
                "Confirmar produto inexistente"
        );
    }

    @Step("Validar estoque do produto")
    public void assertStock(CreatedProduct product, int expectedStock) {
        assertThat(findProduct(product).quantity())
                .as(
                        "Estoque do produto: idProduto=%s, inicial=%d, esperado=%d",
                        product.id(),
                        product.initialStock(),
                        expectedStock
                )
                .isEqualTo(expectedStock);
    }

    public CreatedProduct getByAlias(String alias) {
        return context.getProducts().stream()
                .filter(product -> product.alias().equals(alias))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Produto não preparado no cenário: " + alias
                ));
    }

    private void validatePersistence(CreatedProduct product) {
        ProductResponse persistedProduct = findProduct(product);
        ProductRequest expectedProduct = product.request();

        assertThat(persistedProduct.name())
                .as("Nome do produto persistido")
                .isEqualTo(expectedProduct.name());

        assertThat(persistedProduct.description())
                .as("Descrição do produto persistido")
                .isEqualTo(expectedProduct.description());

        assertThat(persistedProduct.price())
                .as("Preço do produto persistido")
                .isEqualTo(expectedProduct.price());

        assertThat(persistedProduct.quantity())
                .as("Estoque inicial do produto persistido")
                .isEqualTo(product.initialStock());
    }
}