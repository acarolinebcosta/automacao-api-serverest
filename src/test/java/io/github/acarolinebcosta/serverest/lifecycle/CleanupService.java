package io.github.acarolinebcosta.serverest.lifecycle;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.api.MessageResponse;
import io.github.acarolinebcosta.serverest.cart.CartClient;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.github.acarolinebcosta.serverest.product.CreatedProduct;
import io.github.acarolinebcosta.serverest.product.ProductClient;
import io.github.acarolinebcosta.serverest.user.UserClient;
import io.github.acarolinebcosta.serverest.validation.ResponseAssertions;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public final class CleanupService {

    private final ScenarioContext context;
    private final CartClient cartClient;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final CleanupAssertions cleanupAssertions;

    @Step("Executar limpeza dos recursos do cenário")
    public List<Throwable> cleanup() {
        List<Throwable> failures = new ArrayList<>();

        if (context.getToken() != null) {
            if (context.getCartId() != null) {
                attempt(failures, () -> {
                    Response response =
                            cartClient.cancelPurchase(context.getToken());

                    ResponseAssertions.assertStatus(
                            response,
                            200,
                            "Limpar carrinho"
                    );

                    assertThat(response.as(MessageResponse.class).message())
                            .withFailMessage(
                                    "Mensagem inesperada na limpeza do carrinho; ver evidência HTTP sanitizada"
                            )
                            .isIn(
                                    ApiMessages.CANCEL_SUCCESS,
                                    ApiMessages.CART_NOT_FOUND
                            );

                    cleanupAssertions.assertCartAbsent();
                });
            }

            for (CreatedProduct product : context.getProducts()) {
                attempt(failures, () -> deleteAndVerify(
                        "Limpar produto",
                        () -> productClient.delete(
                                context.getToken(),
                                product.id()
                        ),
                        () -> cleanupAssertions.assertProductAbsent(
                                product.id()
                        )
                ));
            }
        }

        for (String auxiliaryUserId : context.getAuxiliaryUserIds()) {
            attempt(failures, () -> deleteAndVerify(
                    "Limpar usuário auxiliar",
                    () -> userClient.delete(auxiliaryUserId),
                    () -> cleanupAssertions.assertUserAbsent(auxiliaryUserId)
            ));
        }

        if (context.getUserId() != null) {
            attempt(failures, () -> deleteAndVerify(
                    "Limpar usuário",
                    () -> userClient.delete(context.getUserId()),
                    cleanupAssertions::assertUserAbsent
            ));
        }

        return List.copyOf(failures);
    }

    private void deleteAndVerify(
            String operation,
            Supplier<Response> deleteOperation,
            Runnable absenceCheck
    ) {
        Response response = deleteOperation.get();

        ResponseAssertions.assertStatus(
                response,
                200,
                operation
        );

        assertThat(response.as(MessageResponse.class).message())
                .withFailMessage(
                        "%s: mensagem inesperada na limpeza; ver evidência HTTP sanitizada",
                        operation
                )
                .isIn(
                        ApiMessages.DELETE_SUCCESS,
                        ApiMessages.NOTHING_DELETED
                );

        absenceCheck.run();
    }

    private void attempt(
            List<Throwable> failures,
            Runnable operation
    ) {
        try {
            operation.run();
        } catch (Exception | AssertionError failure) {
            failures.add(failure);
        }
    }
}