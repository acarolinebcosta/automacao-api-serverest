# language: pt

Funcionalidade: Validação de cenários negativos de carrinho

  Contexto:
    Dado que existe um usuário administrador autenticado com produto disponível

  @negativo @carrinho @CT-NEG-CARRINHO-01
  Cenário: Validar que o cadastro de carrinho sem produtos é rejeitado
    Quando tento cadastrar um carrinho sem produtos
    Então a criação do carrinho deve ser rejeitada com status 400
    E a mensagem "produtos é obrigatório" deve ser retornada para o campo "produtos"

  @negativo @carrinho @CT-NEG-CARRINHO-02
  Cenário: Validar que o cadastro de carrinho com quantidade zero é rejeitado
    Quando tento cadastrar um carrinho com quantidade zero
    Então a criação do carrinho deve ser rejeitada com status 400
    E a mensagem "produtos[0].quantidade deve ser um número positivo" deve ser retornada para o campo "produtos[0].quantidade"

  @negativo @carrinho @CT-NEG-CARRINHO-03
  Cenário: Validar que o cadastro de carrinho com quantidade negativa é rejeitado
    Quando tento cadastrar um carrinho com quantidade negativa
    Então a criação do carrinho deve ser rejeitada com status 400
    E a mensagem "produtos[0].quantidade deve ser um número positivo" deve ser retornada para o campo "produtos[0].quantidade"

  @negativo @carrinho @CT-NEG-CARRINHO-04
  Cenário: Validar que o cadastro de carrinho com produto de ID vazio é rejeitado
    Quando tento cadastrar um carrinho com produto de ID vazio
    Então a criação do carrinho deve ser rejeitada com status 400