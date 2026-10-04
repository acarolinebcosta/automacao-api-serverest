package io.github.acarolinebcosta.serverest.product;

import io.github.acarolinebcosta.serverest.testdata.DataGenerator;

public final class ProductData {

    private ProductData() {
    }

    public static ProductRequest valid(int price, int stock) {
        return new ProductBuilder()
                .withName(DataGenerator.uniqueProductName())
                .withPrice(price)
                .withDescription("Product created by the API automation scenario")
                .withQuantity(stock)
                .build();
    }
}
