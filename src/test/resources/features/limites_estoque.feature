# language: pt

@carrinho @regra-negocio @estoque @boundary
Funcionalidade: Limites de estoque
  Como usuário autenticado do ServeRest
  Quero reservar produtos dentro da quantidade disponível
  Para que a reserva respeite o limite do estoque

  @CT19
  Esquema do Cenário: Validar que a criação do carrinho respeite o limite de estoque disponível
    Dado que existe um produto com estoque 5
    Quando o usuário solicita <quantidade> unidades
    Então a criação deve resultar em "<resultado>"

    Exemplos:
      | quantidade | resultado |
      | 4          | sucesso   |
      | 5          | sucesso   |
      | 6          | rejeitado |
