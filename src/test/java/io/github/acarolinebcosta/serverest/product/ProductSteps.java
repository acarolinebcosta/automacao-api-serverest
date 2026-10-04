package io.github.acarolinebcosta.serverest.product;

import io.github.acarolinebcosta.serverest.user.UserService;
import io.cucumber.java.pt.Dado;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ProductSteps {

    private static final int MAIN_PRODUCT_PRICE = 100;
    private static final int FIRST_MULTI_PRODUCT_PRICE = 100;
    private static final int SECOND_MULTI_PRODUCT_PRICE = 50;

    private static final int AVAILABLE_STOCK = 10;
    private static final int LIMITED_STOCK = 5;
    private static final int FIRST_MULTI_PRODUCT_STOCK = 10;

    private final UserService userService;
    private final ProductService productService;

    @Dado("que existe um usuário administrador autenticado com produto disponível")
    @Dado("que existe um produto disponível")
    public void availableProductExists() {
        prepareMainProduct(AVAILABLE_STOCK);
    }

    @Dado("que existe um produto com estoque limitado")
    public void limitedStockProductExists() {
        prepareMainProduct(LIMITED_STOCK);
    }

    @Dado("que existe um produto com estoque {int}")
    public void productWithStockExists(int stock) {
        prepareMainProduct(stock);
    }

    @Dado("que existem produtos disponíveis para compra")
    public void multipleProductsAreAvailable() {
        prepareTwoProducts(AVAILABLE_STOCK);
    }

    @Dado("que existem produtos com estoques distintos para compra")
    public void productsWithDifferentStocksExist() {
        prepareTwoProducts(LIMITED_STOCK);
    }

    private void prepareMainProduct(int stock) {
        userService.createAuthenticatedAdmin();

        productService.createProduct(
                ProductAliases.MAIN,
                MAIN_PRODUCT_PRICE,
                stock
        );
    }

    private void prepareTwoProducts(int secondProductStock) {
        userService.createAuthenticatedAdmin();

        productService.createProduct(
                ProductAliases.MULTI_A,
                FIRST_MULTI_PRODUCT_PRICE,
                FIRST_MULTI_PRODUCT_STOCK
        );

        productService.createProduct(
                ProductAliases.MULTI_B,
                SECOND_MULTI_PRODUCT_PRICE,
                secondProductStock
        );
    }
}