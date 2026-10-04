package io.github.acarolinebcosta.serverest.product;

import io.github.acarolinebcosta.serverest.user.UserService;
import io.cucumber.java.pt.Dado;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ProductSteps {

    private final UserService userService;
    private final ProductService productService;

    @Dado("que existe um usuário administrador autenticado com produto disponível")
    @Dado("que existe um produto disponível")
    public void availableProductExists() {
        prepareMainProduct(10);
    }

    @Dado("que existe um produto com estoque limitado")
    public void limitedStockProductExists() {
        prepareMainProduct(5);
    }

    @Dado("que existe um produto com estoque {int}")
    public void productWithStockExists(int stock) {
        prepareMainProduct(stock);
    }

    @Dado("que existem produtos disponíveis para compra")
    public void multipleProductsAreAvailable() {
        prepareTwoProducts(10);
    }

    @Dado("que existem produtos com estoques distintos para compra")
    public void productsWithDifferentStocksExist() {
        prepareTwoProducts(5);
    }

    private void prepareMainProduct(int stock) {
        userService.createAuthenticatedAdmin();
        productService.createProduct("principal", 100, stock);
    }

    private void prepareTwoProducts(int secondStock) {
        userService.createAuthenticatedAdmin();
        productService.createProduct("A", 100, 10);
        productService.createProduct("B", 50, secondStock);
    }
}
