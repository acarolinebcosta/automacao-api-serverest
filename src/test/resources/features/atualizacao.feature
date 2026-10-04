# language: pt

Funcionalidade: Validação de atualização de usuários via PUT

  @put @CT-PUT-01
  Cenário: Validar que a atualização de um usuário existente é aprovada
    Dado que existe um usuário administrador autenticado
    Quando atualizo o usuário com dados válidos
    Então a atualização deve ser aprovada com status 200
    E a mensagem de atualização com sucesso deve ser retornada

  @put @CT-PUT-02
  Cenário: Validar que a atualização de um usuário inexistente cria um novo registro
    Dado que existe um usuário administrador autenticado
    Quando atualizo um usuário com ID inexistente e dados válidos
    Então a atualização deve criar um novo registro com status 201
    E a mensagem de cadastro com sucesso deve ser retornada

  @negativo @put @CT-PUT-03
  Cenário: Validar que a atualização de usuário sem o campo nome é rejeitada
    Dado que existe um usuário administrador autenticado
    Quando atualizo o usuário sem o campo nome
    Então a atualização deve ser rejeitada com status 400
    E a mensagem "nome é obrigatório" deve ser retornada para o campo "nome"

  @put @CT-PUT-04
  Cenário: Validar que a atualização de usuário sem token é aceita pela API
    Quando atualizo um usuário sem token
    Então a atualização deve criar um novo registro com status 201
    E a mensagem de cadastro com sucesso deve ser retornada
