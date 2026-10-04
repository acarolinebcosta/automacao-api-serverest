package io.github.acarolinebcosta.serverest.product;

public final class ProductBuilder {

    private String name;
    private Integer price;
    private String description;
    private Integer quantity;

    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withPrice(Integer price) {
        this.price = price;
        return this;
    }

    public ProductBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public ProductBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public ProductRequest build() {
        return new ProductRequest(name, price, description, quantity);
    }
}
