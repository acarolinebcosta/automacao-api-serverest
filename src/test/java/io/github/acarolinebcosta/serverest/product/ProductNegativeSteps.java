package io.github.acarolinebcosta.serverest.product;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ProductNegativeSteps {

    private final ProductService productService;
    private final ProductDataFactory productDataFactory;
    private final ScenarioContext context;

    private String existingProductName;

    @Dado("que existe um produto cadastrado com nome conhecido")
    public void productWithKnownNameExists() {
        existingProductName = productDataFactory.createAndReturnName();
    }

    @Quando("tento cadastrar um produto sem o campo {string}")
    public void attemptCreateProductWithoutField(String field) {
        context.setLastResponse(productService.attemptCreateWithoutField(field));
    }

    @Quando("tento cadastrar um produto com preço negativo")
    public void attemptCreateProductWithNegativePrice() {
        context.setLastResponse(productService.attemptCreateWithNegativePrice());
    }

    @Quando("tento cadastrar um produto com quantidade negativa")
    public void attemptCreateProductWithNegativeQuantity() {
        context.setLastResponse(productService.attemptCreateWithNegativeQuantity());
    }

    @Quando("tento cadastrar outro produto com o mesmo nome")
    public void attemptCreateProductWithDuplicateName() {
        context.setLastResponse(
                productService.attemptCreateWithDuplicateName(existingProductName)
        );
    }

    @Quando("busco um produto com ID inexistente")
    public void findNonexistentProduct() {
        context.setLastResponse(productService.findNonexistentProduct());
    }

    @Entao("a criação do produto deve ser rejeitada com status 400")
    public void productCreationShouldBeRejected() {
        Response response = context.getLastResponse();

        ResponseAssertions.assertStatus(response, 400, "Rejeitar criação de produto");
    }
}