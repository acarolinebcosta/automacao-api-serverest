# language: pt

@fluxo-principal @compra @estoque
Funcionalidade: Conclusão de compra
  Como usuário autenticado do ServeRest
  Quero adicionar produtos ao carrinho
  Para concluir uma compra mantendo o estoque consumido

  @CT-E2E-01 @conclusao @CT01 @CT02 @CT03 @CT04 @CT05 @CT06 @CT07 @CT08 @CT09 @CT10 @CT11
  Cenário: Validar que a conclusão de uma compra mantenha o estoque consumido
    Dado que existe um usuário administrador autenticado com produto disponível
    Quando ele realiza uma compra
    Então a compra deve ser concluída mantendo o estoque consumido
