# language: pt

Funcionalidade: Validação de cenários negativos de produto

  Contexto:
    Dado que existe um usuário administrador autenticado

  @negativo @produto @CT-NEG-PRODUTO-01
  Cenário: Validar que o cadastro de produto sem o campo nome é rejeitado
    Quando tento cadastrar um produto sem o campo "nome"
    Então a criação do produto deve ser rejeitada com status 400
    E a mensagem "nome é obrigatório" deve ser retornada para o campo "nome"

  @negativo @produto @CT-NEG-PRODUTO-02
  Cenário: Validar que o cadastro de produto sem o campo preço é rejeitado
    Quando tento cadastrar um produto sem o campo "preco"
    Então a criação do produto deve ser rejeitada com status 400
    E a mensagem "preco é obrigatório" deve ser retornada para o campo "preco"

  @negativo @produto @CT-NEG-PRODUTO-03
  Cenário: Validar que o cadastro de produto sem o campo descrição é rejeitado
    Quando tento cadastrar um produto sem o campo "descricao"
    Então a criação do produto deve ser rejeitada com status 400
    E a mensagem "descricao é obrigatório" deve ser retornada para o campo "descricao"

  @negativo @produto @CT-NEG-PRODUTO-04
  Cenário: Validar que o cadastro de produto sem o campo quantidade é rejeitado
    Quando tento cadastrar um produto sem o campo "quantidade"
    Então a criação do produto deve ser rejeitada com status 400
    E a mensagem "quantidade é obrigatório" deve ser retornada para o campo "quantidade"

  @negativo @produto @CT-NEG-PRODUTO-05
  Cenário: Validar que o cadastro de produto com preço negativo é rejeitado
    Quando tento cadastrar um produto com preço negativo
    Então a criação do produto deve ser rejeitada com status 400
    E a mensagem "preco deve ser um número positivo" deve ser retornada para o campo "preco"

  @negativo @produto @CT-NEG-PRODUTO-06
  Cenário: Validar que o cadastro de produto com quantidade negativa é rejeitado
    Quando tento cadastrar um produto com quantidade negativa
    Então a criação do produto deve ser rejeitada com status 400
    E a mensagem "quantidade deve ser maior ou igual a 0" deve ser retornada para o campo "quantidade"

  @negativo @produto @CT-NEG-PRODUTO-07
  Cenário: Validar que o cadastro de produto com nome duplicado é rejeitado
    Dado que existe um produto cadastrado com nome conhecido
    Quando tento cadastrar outro produto com o mesmo nome
    Então a criação do produto deve ser rejeitada com status 400
    E a mensagem "Já existe produto com esse nome" deve ser retornada

  @negativo @produto @CT-NEG-PRODUTO-08
  Cenário: Validar que a busca de produto com ID inexistente é rejeitada
    Quando busco um produto com ID inexistente
    Então a operação deve ser rejeitada com status 400
    E a mensagem "Produto não encontrado" deve ser retornada