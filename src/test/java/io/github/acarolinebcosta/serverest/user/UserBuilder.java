package io.github.acarolinebcosta.serverest.user;

public final class UserBuilder {

    private String name;
    private String email;
    private String password;
    private String administrator;

    public UserBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder withAdministrator(String administrator) {
        this.administrator = administrator;
        return this;
    }

    public UserRequest build() {
        return new UserRequest(name, email, password, administrator);
    }
}
