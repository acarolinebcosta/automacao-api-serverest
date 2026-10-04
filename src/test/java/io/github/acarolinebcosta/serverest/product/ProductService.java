package io.github.acarolinebcosta.serverest.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.api.CreateResponse;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.testdata.DataGenerator;
import io.github.acarolinebcosta.serverest.validation.ContractAssertions;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ProductService {

    private final ScenarioContext context;
    private final ProductClient productClient;
    private final ContractAssertions contractAssertions;

    @Step("Criar produto e confirmar estoque inicial")
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
                request
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

    @Step("Tentar cadastrar produto sem o campo {string}")
    public Response attemptCreateWithoutField(String field) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("nome", DataGenerator.uniqueProductName());
        payload.put("preco", 100);
        payload.put("descricao", "produto negativo");
        payload.put("quantidade", 10);
        payload.remove(field);

        return productClient.createRaw(context.getToken(), payload);
    }

    @Step("Tentar cadastrar produto com preço negativo")
    public Response attemptCreateWithNegativePrice() {
        return productClient.createRaw(context.getToken(), Map.of(
                "nome", DataGenerator.uniqueProductName(),
                "preco", -1,
                "descricao", "produto negativo",
                "quantidade", 10
        ));
    }

    @Step("Tentar cadastrar produto com quantidade negativa")
    public Response attemptCreateWithNegativeQuantity() {
        return productClient.createRaw(context.getToken(), Map.of(
                "nome", DataGenerator.uniqueProductName(),
                "preco", 100,
                "descricao", "produto negativo",
                "quantidade", -1
        ));
    }

    @Step("Tentar cadastrar produto com nome duplicado")
    public Response attemptCreateWithDuplicateName(String existingName) {
        return productClient.createRaw(context.getToken(), Map.of(
                "nome", existingName,
                "preco", 100,
                "descricao", "produto duplicado",
                "quantidade", 10
        ));
    }

    @Step("Buscar produto com ID inexistente")
    public Response findNonexistentProduct() {
        return productClient.findById(DataGenerator.nonexistentProductId());
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
    @Step("Excluir produto")
    public Response deleteProduct(CreatedProduct product) {
        return productClient.delete(context.getToken(), product.id());
}
}