package io.github.acarolinebcosta.serverest.context;

import io.github.acarolinebcosta.serverest.cart.CartRequest;
import io.github.acarolinebcosta.serverest.product.CreatedProduct;
import io.github.acarolinebcosta.serverest.user.UserRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Per-scenario state shared between step definitions, services and assertions.
 *
 * Cucumber creates a new instance per scenario through dependency injection,
 * preventing state from leaking between scenarios. Mutable fields are exposed
 * through Lombok getters and setters, while the product list is read-only from
 * the outside and can only be appended through
 * {@link #addProduct(CreatedProduct)}.
 */
@Getter
public final class ScenarioContext {

    @Setter
    private UserRequest user;

    @Setter
    private String userId;

    @Setter
    private String token;

    @Setter
    private String cartId;

    @Setter
    private CartRequest activeCartRequest;

    private final List<CreatedProduct> products = new ArrayList<>();

    public List<CreatedProduct> getProducts() {
        return List.copyOf(products);
    }

    public void addProduct(@NonNull CreatedProduct product) {
        products.add(product);
    }

    /**
     * Returns the product registered in this scenario with the given ID.
     *
     * @param productId identifier of the product to look up
     * @return the matching product
     * @throws IllegalStateException if no product with that ID was added
     *                               to the scenario
     */
    public CreatedProduct requireProduct(String productId) {
        return products.stream()
                .filter(product -> product.id().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "O produto não foi criado no cenário: " + productId
                ));
    }
}