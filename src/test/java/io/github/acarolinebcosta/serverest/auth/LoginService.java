package io.github.acarolinebcosta.serverest.auth;

import io.github.acarolinebcosta.serverest.testdata.DataGenerator;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class LoginService {

    private final LoginClient loginClient;

    @Step("Tentar autenticar com senha incorreta")
    public Response loginWithInvalidPassword(String email) {
        return loginClient.loginRaw(Map.of(
                "email", email,
                "password", DataGenerator.uniquePassword()
        ));
    }

    @Step("Tentar autenticar com e-mail não cadastrado")
    public Response loginWithUnknownEmail() {
        return loginClient.loginRaw(Map.of(
                "email", DataGenerator.uniqueEmail(),
                "password", DataGenerator.uniquePassword()
        ));
    }

    @Step("Tentar autenticar sem o campo {string}")
    public Response loginWithoutField(String field) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("email", DataGenerator.uniqueEmail());
        payload.put("password", DataGenerator.uniquePassword());
        payload.remove(field);

        return loginClient.loginRaw(payload);
    }
}