# language: pt

Funcionalidade: Validação de integridade entre recursos

  @negativo @integridade @CT-NEG-INTEG-01
  Cenário: Validar que a exclusão de produto em carrinho ativo é rejeitada
    Dado que existe um usuário administrador autenticado com produto disponível
    E o usuário possui um carrinho ativo com o produto principal
    Quando tento excluir o produto principal que está em uso
    Então a operação deve ser rejeitada com status 400
    E a mensagem de produto em uso deve ser retornada

  @negativo @integridade @CT-NEG-INTEG-02
  Cenário: Validar que a exclusão de usuário com carrinho ativo é rejeitada
    Dado que existe um usuário administrador autenticado com produto disponível
    E o usuário possui um carrinho ativo com o produto principal
    Quando tento excluir o usuário que possui carrinho
    Então a operação deve ser rejeitada com status 400
    E a mensagem de usuário com carrinho deve ser retornada