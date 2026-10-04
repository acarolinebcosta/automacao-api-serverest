# language: pt

Funcionalidade: Validação de cenários negativos de usuário

  @negativo @usuario @CT-NEG-USUARIO-01
  Cenário: Validar que o cadastro de usuário com e-mail já utilizado é rejeitado
    Dado que existe um usuário cadastrado com e-mail conhecido
    Quando tento cadastrar um novo usuário com o mesmo e-mail
    Então a criação do usuário deve ser rejeitada com status 400
    E a mensagem de e-mail duplicado deve ser retornada

  @negativo @usuario @CT-NEG-USUARIO-02
  Cenário: Validar que o cadastro de usuário sem o campo nome é rejeitado
    Quando tento cadastrar um usuário sem o campo nome
    Então a criação do usuário deve ser rejeitada com status 400

  @negativo @usuario @CT-NEG-USUARIO-03
  Cenário: Validar que o cadastro de usuário sem o campo e-mail é rejeitado
    Quando tento cadastrar um usuário sem o campo e-mail
    Então a criação do usuário deve ser rejeitada com status 400

  @negativo @usuario @CT-NEG-USUARIO-04
  Cenário: Validar que o cadastro de usuário sem o campo senha é rejeitado
    Quando tento cadastrar um usuário sem o campo senha
    Então a criação do usuário deve ser rejeitada com status 400

  @negativo @usuario @CT-NEG-USUARIO-05
  Cenário: Validar que o cadastro de usuário sem o campo administrador é rejeitado
    Quando tento cadastrar um usuário sem o campo administrador
    Então a criação do usuário deve ser rejeitada com status 400

  @negativo @usuario @CT-NEG-USUARIO-06
  Cenário: Validar que a busca de usuário com ID inexistente é rejeitada
    Quando busco um usuário com ID inexistente
    Então a operação deve ser rejeitada com status 400
    E a mensagem de usuário não encontrado deve ser retornada