# language: pt

@fluxo-principal @cancelamento @estoque
Funcionalidade: Cancelamento de compra
  Como usuário autenticado do ServeRest
  Quero cancelar um carrinho
  Para que o estoque reservado seja restaurado

  @CT-E2E-02 @CT01 @CT02 @CT03 @CT04 @CT05 @CT06 @CT07 @CT08 @CT12 @CT13 @CT14 @CT15
  Cenário: Validar que o cancelamento de uma compra restaure o estoque
    Dado que existe um usuário administrador autenticado com produto disponível
    Quando ele cria e cancela uma compra
    Então o carrinho deve ser removido e o estoque restaurado
