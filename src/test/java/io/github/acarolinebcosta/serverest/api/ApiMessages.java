package io.github.acarolinebcosta.serverest.api;

public final class ApiMessages {

    public static final String CREATE_SUCCESS = "Cadastro realizado com sucesso";
    public static final String LOGIN_SUCCESS = "Login realizado com sucesso";
    public static final String CANCEL_SUCCESS =
            "Registro excluído com sucesso. Estoque dos produtos reabastecido";
    public static final String INSUFFICIENT_STOCK = "Produto não possui quantidade suficiente";
    public static final String SECOND_CART = "Não é permitido ter mais de 1 carrinho";
    public static final String PRODUCT_DUPLICATED = "Não é permitido possuir produto duplicado";

    private ApiMessages() {
    }

    public static final String DELETE_SUCCESS =
            "Registro excluído com sucesso";

    public static final String NOTHING_DELETED =
            "Nenhum registro excluído";

    public static final String CART_NOT_FOUND =
            "Não foi encontrado carrinho para esse usuário";

    public static final String PRODUCT_NOT_FOUND =
            "Produto não encontrado";

    public static final String USER_NOT_FOUND =
            "Usuário não encontrado";
}
