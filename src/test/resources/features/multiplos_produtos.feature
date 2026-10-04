# language: pt

@carrinho @multiplos-produtos @estoque
Funcionalidade: Carrinho com múltiplos produtos
  Como usuário autenticado do ServeRest
  Quero reservar produtos diferentes em uma única operação
  Para que os totais e os estoques permaneçam consistentes

  @CT-MULTI-01 @cancelamento
  Cenário: Validar que um carrinho com múltiplos produtos calcule os totais e restaure os estoques
    Dado que existem produtos disponíveis para compra
    Quando o usuário adiciona múltiplos produtos ao carrinho
    Então o carrinho deve calcular os totais e estoques corretamente

  @CT-MULTI-02 @atomicidade @regra-negocio
  Cenário: Validar que uma solicitação com estoque insuficiente seja rejeitada sem alterar os estoques
    Dado que existem produtos com estoques distintos para compra
    Quando o usuário solicita múltiplos produtos excedendo um dos estoques
    Então a operação deve ser rejeitada sem alterar o estoque
