package io.github.acarolinebcosta.serverest.context;

import io.github.acarolinebcosta.serverest.cart.CartRequest;
import io.github.acarolinebcosta.serverest.product.CreatedProduct;
import io.github.acarolinebcosta.serverest.user.UserRequest;
import io.restassured.response.Response;
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
    private String knownEmail;

    @Setter
    private Response lastResponse;

    private final List<CreatedProduct> products = new ArrayList<>();
    private final List<String> auxiliaryUserIds = new ArrayList<>();

    public List<CreatedProduct> getProducts() {
        return List.copyOf(products);
    }

    public void addProduct(CreatedProduct product) {
        products.add(Objects.requireNonNull(product, "O produto não pode ser nulo"));
    }

    public CreatedProduct requireProduct(String productId) {
        return products.stream()
                .filter(product -> product.id().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Produto não preparado no cenário: " + productId
                ));
    }

    public List<String> getAuxiliaryUserIds() {
        return List.copyOf(auxiliaryUserIds);
    }

    public void addAuxiliaryUser(String userId) {
        auxiliaryUserIds.add(Objects.requireNonNull(
                userId,
                "O ID do usuário auxiliar não pode ser nulo"
        ));
    }
}