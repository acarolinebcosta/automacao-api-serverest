package io.github.acarolinebcosta.serverest.lifecycle;

import io.github.acarolinebcosta.serverest.api.ApiMessages;
import io.github.acarolinebcosta.serverest.api.MessageResponse;
import io.github.acarolinebcosta.serverest.cart.CartClient;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
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

    @Step("Cleanup scenario resources")
    public List<Throwable> cleanup() {
        List<Throwable> failures = new ArrayList<>();
        if (context.getToken() != null) {
            attempt(failures, () -> {
                Response response = cartClient.cancelPurchase(context.getToken());
                ResponseAssertions.assertStatus(response, 200, "Cleanup cart");
                assertThat(response.as(MessageResponse.class).message())
                        .withFailMessage("Unexpected cart cleanup message; see sanitized HTTP evidence")
                        .isIn(ApiMessages.CANCEL_SUCCESS, ApiMessages.CART_NOT_FOUND);
                cleanupAssertions.assertCartAbsent();
            });
            for (var product : context.getProducts()) {
                attempt(failures, () -> deleteAndVerify("Cleanup product",
                        () -> productClient.delete(context.getToken(), product.id()),
                        () -> cleanupAssertions.assertProductAbsent(product.id())));
            }
        }
        if (context.getUserId() != null) {
            attempt(failures, () -> deleteAndVerify("Cleanup user",
                    () -> userClient.delete(context.getUserId()), cleanupAssertions::assertUserAbsent));
        }
        return List.copyOf(failures);
    }

    private void deleteAndVerify(String action, Supplier<Response> deleteOperation, Runnable absenceCheck) {
        Response response = deleteOperation.get();
        ResponseAssertions.assertStatus(response, 200, action);
        assertThat(response.as(MessageResponse.class).message())
                .withFailMessage("%s: unexpected cleanup message; see sanitized HTTP evidence", action)
                .isIn(ApiMessages.DELETE_SUCCESS, ApiMessages.NOTHING_DELETED);
        absenceCheck.run();
    }

    private void attempt(List<Throwable> failures, Runnable operation) {
        try {
            operation.run();
        } catch (Exception | AssertionError failure) {
            failures.add(failure);
        }
    }
}
