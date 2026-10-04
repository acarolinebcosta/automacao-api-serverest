# language: pt

@usuario @cadastro
Funcionalidade: Cadastro de usuário
  Como consumidor da API ServeRest
  Quero cadastrar um usuário administrador
  Para utilizar uma identidade persistida nos demais recursos

  @CT-USUARIO-01 @CT01
  Cenário: Validar que um usuário administrador seja criado e persistido com sucesso
    Dado que possuo os dados de um novo usuário administrador
    Quando realizo o cadastro do usuário
    Então o usuário deve ser criado com sucesso
    E os dados do usuário devem estar persistidos
