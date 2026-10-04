# language: pt

@carrinho @regra-negocio
Funcionalidade: Regras de criação do carrinho
  Como usuário autenticado do ServeRest
  Quero reservar somente produtos disponíveis em um único carrinho
  Para que operações rejeitadas preservem o carrinho e o estoque existentes

  @CT16 @estoque
  Cenário: Validar que um segundo carrinho para o mesmo usuário seja rejeitado
    Dado que o usuário já possui um carrinho
    Quando tenta criar outro carrinho
    Então a operação deve ser rejeitada preservando o primeiro carrinho

  @CT17
  Cenário: Validar que um carrinho com produto inexistente seja rejeitado
    Dado que existe um usuário administrador autenticado
    Quando tenta criar um carrinho com produto inexistente
    Então a operação deve ser rejeitada sem criar carrinho

  @CT18 @estoque
  Cenário: Validar que uma quantidade superior ao estoque seja rejeitada sem alterar o estoque
    Dado que existe um produto com estoque limitado
    Quando o usuário solicita uma quantidade superior ao estoque
    Então a operação deve ser rejeitada sem alterar o estoque

  @CT20 @estoque
  Cenário: Validar que produtos duplicados no mesmo carrinho sejam rejeitados sem alterar o estoque
    Dado que existe um produto disponível
    Quando o usuário informa o mesmo produto duas vezes no carrinho
    Então a operação deve ser rejeitada sem alterar o estoque
