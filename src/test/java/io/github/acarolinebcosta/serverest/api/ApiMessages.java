package io.github.acarolinebcosta.serverest.api;

public final class ApiMessages {

    public static final String CREATE_SUCCESS =
            "Cadastro realizado com sucesso";

    public static final String LOGIN_SUCCESS =
            "Login realizado com sucesso";

    public static final String DELETE_SUCCESS =
            "Registro excluído com sucesso";

    public static final String CANCEL_SUCCESS =
            "Registro excluído com sucesso. Estoque dos produtos reabastecido";

    public static final String NOTHING_DELETED =
            "Nenhum registro excluído";

    public static final String INSUFFICIENT_STOCK =
            "Produto não possui quantidade suficiente";

    public static final String SECOND_CART =
            "Não é permitido ter mais de 1 carrinho";

    public static final String PRODUCT_DUPLICATED =
            "Não é permitido possuir produto duplicado";

    public static final String CART_NOT_FOUND =
            "Não foi encontrado carrinho para esse usuário";

    public static final String PRODUCT_NOT_FOUND =
            "Produto não encontrado";

    public static final String USER_NOT_FOUND =
            "Usuário não encontrado";

    public static final String DUPLICATE_EMAIL =
            "Este email já está sendo usado";

    public static final String INVALID_CREDENTIALS =
            "Email e/ou senha inválidos";

    public static final String PRODUCT_DUPLICATE_NAME =
            "Já existe produto com esse nome";

    public static final String REQUIRED_FIELD =
            "é obrigatório";

    public static final String POSITIVE_NUMBER =
            "deve ser um número positivo";

    public static final String INVALID_ID_FORMAT =
            "id deve ter exatamente 16 caracteres alfanuméricos";

    private ApiMessages() {
        throw new UnsupportedOperationException("Utility class");
    }
}