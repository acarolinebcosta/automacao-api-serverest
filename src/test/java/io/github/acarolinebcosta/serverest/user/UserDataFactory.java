package io.github.acarolinebcosta.serverest.user;

import io.github.acarolinebcosta.serverest.api.CreateResponse;
import io.github.acarolinebcosta.serverest.context.ScenarioContext;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

/**
 * Factory that creates auxiliary users for negative scenarios and returns
 * their identifiers.
 * <p>
 * Auxiliary users are registered in {@link ScenarioContext} so that
 * {@code CleanupService} can remove them at the end of the scenario, avoiding
 * orphan data on the public ServeRest instance.
 */
@RequiredArgsConstructor
public final class UserDataFactory {

    private final UserClient userClient;
    private final ScenarioContext context;

    public String createAndReturnEmail() {
        UserRequest user = UserData.validAdmin();
        Response response = userClient.create(user);

        if (response.statusCode() == 201) {
            CreateResponse created = response.as(CreateResponse.class);
            context.addAuxiliaryUser(created.id());
        }

        return user.email();
    }
}