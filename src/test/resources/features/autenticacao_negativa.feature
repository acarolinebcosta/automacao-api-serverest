# language: pt

Funcionalidade: Validação de cenários negativos de autenticação

  @negativo @login @CT-NEG-LOGIN-01
  Cenário: Validar que o login com senha incorreta é rejeitado
    Dado que existe um usuário cadastrado com e-mail conhecido
    Quando tento autenticar com uma senha incorreta
    Então a autenticação deve ser rejeitada com status 401
    E a mensagem de credenciais inválidas deve ser retornada

  @negativo @login @CT-NEG-LOGIN-02
  Cenário: Validar que o login com e-mail não cadastrado é rejeitado
    Quando tento autenticar com um e-mail não cadastrado
    Então a autenticação deve ser rejeitada com status 401
    E a mensagem de credenciais inválidas deve ser retornada

  @negativo @login @CT-NEG-LOGIN-03
  Cenário: Validar que o login sem o campo e-mail é rejeitado
    Quando tento autenticar sem o campo e-mail
    Então a autenticação deve ser rejeitada com status 400

  @negativo @login @CT-NEG-LOGIN-04
  Cenário: Validar que o login sem o campo senha é rejeitado
    Quando tento autenticar sem o campo senha
    Então a autenticação deve ser rejeitada com status 400