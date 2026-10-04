# language: pt

Funcionalidade: Validação de autorização em endpoints protegidos

  @negativo @autorizacao @CT-NEG-AUTH-01
  Cenário: Validar que a criação de produto sem token é rejeitada
    Quando tento criar um produto sem token
    Então a operação deve ser rejeitada com status 401
    E a mensagem de token inválido deve ser retornada

  @negativo @autorizacao @CT-NEG-AUTH-02
  Cenário: Validar que a criação de produto com token inválido é rejeitada
    Quando tento criar um produto com token inválido
    Então a operação deve ser rejeitada com status 401
    E a mensagem de token inválido deve ser retornada

  @negativo @autorizacao @CT-NEG-AUTH-03
  Cenário: Validar que a exclusão de produto sem token é rejeitada
    Quando tento excluir um produto sem token
    Então a operação deve ser rejeitada com status 401
    E a mensagem de token inválido deve ser retornada

  @negativo @autorizacao @CT-NEG-AUTH-04
  Cenário: Validar que a criação de carrinho sem token é rejeitada
    Quando tento criar um carrinho sem token
    Então a operação deve ser rejeitada com status 401
    E a mensagem de token inválido deve ser retornada

  @negativo @autorizacao @CT-NEG-AUTH-05
  Cenário: Validar que a conclusão de compra sem token é rejeitada
    Quando tento concluir uma compra sem token
    Então a operação deve ser rejeitada com status 401
    E a mensagem de token inválido deve ser retornada

  @negativo @autorizacao @CT-NEG-AUTH-06
  Cenário: Validar que o cancelamento de compra sem token é rejeitado
    Quando tento cancelar uma compra sem token
    Então a operação deve ser rejeitada com status 401
    E a mensagem de token inválido deve ser retornada