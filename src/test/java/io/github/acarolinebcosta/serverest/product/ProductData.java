package io.github.acarolinebcosta.serverest.product;

import io.github.acarolinebcosta.serverest.testdata.DataGenerator;

public final class ProductData {
    
    public static ProductRequest valid(int price, int stock) {
        return validWithName(DataGenerator.uniqueProductName(), price, stock);
    }
    public static ProductRequest validWithName(String name, int price, int stock) {
        return new ProductBuilder()
                .withName(name)
                .withPrice(price)
                .withDescription("Produto criado pela automação de testes de API")
                .withQuantity(stock)
                .build();
    }

    private ProductData() {
        throw new UnsupportedOperationException("Utility class");
    }
}