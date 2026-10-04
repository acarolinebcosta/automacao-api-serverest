package io.github.acarolinebcosta.serverest.context;

import io.github.acarolinebcosta.serverest.cart.CartRequest;
import io.github.acarolinebcosta.serverest.product.CreatedProduct;
import io.github.acarolinebcosta.serverest.user.UserRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public final class ScenarioContext {

    private UserRequest user;
    private String userId;
    private String token;
    private String cartId;
    private CartRequest activeCartRequest;

    private final List<CreatedProduct> products = new ArrayList<>();

    public List<CreatedProduct> getProducts() {
        return List.copyOf(products);
    }

    public void addProduct(CreatedProduct product) {
        products.add(Objects.requireNonNull(product, "Product must not be null"));
    }
}
