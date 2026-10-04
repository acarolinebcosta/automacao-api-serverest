# ServeRest API Quality Automation

Automação de testes de API construída sobre a [ServeRest](https://serverest.dev), cobrindo o fluxo de compra ponta a ponta (usuário → login → produto → carrinho → conclusão/cancelamento) e cenários adicionais priorizados por risco.

Java 21 · REST Assured · Cucumber · JUnit 5 · JSON Schema · Allure · GitHub Actions.

---

## Fluxo principal

```text
Usuário → Autenticação → Produto → Carrinho → Conclusão ou cancelamento
```

Além do fluxo solicitado, a suíte cobre regras críticas de estoque, atomicidade, duplicidade, persistência, múltiplos produtos, condições de fronteira, efeitos colaterais após rejeição e segurança das evidências.

---

## Stack

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem e runtime |
| Maven | Build e dependências |
| REST Assured | Automação HTTP |
| Cucumber | Especificação BDD |
| JUnit 5 | Execução e ciclo de testes |
| PicoContainer | Injeção de dependências entre steps e serviços |
| AssertJ | Assertions |
| JSON Schema Validator | Validação de contratos |
| Jackson | Serialização JSON |
| Allure | Relatórios e evidências |
| Lombok | Redução de boilerplate |
| GitHub Actions | Integração contínua |

---

## API e endpoints

Base URL padrão: `https://serverest.dev`. Configurável via `-DbaseUrl=<url>`. Timeout padrão de 15000 ms, configurável via `-Dapi.timeout.ms=<ms>`.

| Recurso | Métodos |
|---|---|
| Usuários | `POST /usuarios` · `GET /usuarios/{id}` · `DELETE /usuarios/{id}` |
| Login | `POST /login` |
| Produtos | `POST /produtos` · `GET /produtos/{id}` · `DELETE /produtos/{id}` |
| Carrinhos | `POST /carrinhos` · `GET /carrinhos` · `DELETE /carrinhos/concluir-compra` · `DELETE /carrinhos/cancelar-compra` |

---

## Estratégia de testes

A estratégia parte do fluxo de negócio e dos riscos associados às operações de carrinho e estoque.

As validações não se limitam ao status HTTP. Sempre que aplicável, a automação verifica:

- mensagem de negócio e contrato JSON;
- estado persistido e identificação do recurso criado;
- proprietário, itens, quantidades e preços do carrinho;
- quantidade total e preço total;
- estoque antes e depois da operação;
- inexistência de efeitos colaterais em cenários rejeitados.

Os resultados esperados são calculados de forma independente a partir dos dados controlados pela automação, sem reutilizar a resposta da API como fonte da expectativa.

---
## Análise de qualidade

### Cenários mais críticos

**Fluxo completo usuário → login → produto → carrinho → conclusão**
É o fluxo de negócio obrigatório. Qualquer falha em qualquer etapa
inviabiliza a compra. Valida encadeamento de dados (token, userId,
productId) e persistência ponta a ponta.

**Cancelamento com restauração de estoque**
Afeta diretamente o inventário. Um bug silencioso aqui gera divergência
entre o estoque real e o reportado pela API. O teste valida estoque
antes e depois da operação.

**Atomicidade em carrinho com múltiplos produtos (CT-MULTI-02)**
Um item com estoque insuficiente não pode gerar atualização parcial nos
demais. É o cenário que mais expõe problemas de transação.

### Riscos identificados

- **Inconsistência de estoque** em operações rejeitadas.
- **Dados órfãos** deixados por cenários que falham no meio.
- **Vazamento de credenciais** em relatórios Allure.
- **Quebra de contrato** não detectada até o consumidor falhar.

### O que foi testado

Fluxo principal ponta a ponta, cenários negativos em todos os domínios,
condições de fronteira de estoque, atomicidade, integridade entre
recursos, autorização, contract testing via JSON Schema e sanitização
de evidências.

### O que não foi testado

- **Rate limiting** — API pública; testar pode bloquear execuções.
- **Performance e carga** — exigem ferramenta específica e ambiente controlado.
- **Fuzzing de payload** — baixo retorno para o fluxo de negócio.
- **Permissões por perfil** — a ServeRest não implementa RBAC.
- **Respostas 5xx** — não provocadas artificialmente.
---

### Cobertura solicitada

| Domínio | Verificações |
|---|---|
| Usuário | Criação, status, mensagem, ID, consulta posterior, dados persistidos |
| Autenticação | Login do usuário criado, contrato, mensagem, obtenção e reuso do Bearer token |
| Produto | Criação, persistência, atributos, estoque inicial |
| Carrinho | Criação, consulta, proprietário, produtos, quantidades, totais, redução de estoque |
| Conclusão | Carrinho removido, estoque permanece consumido |
| Cancelamento | Carrinho removido, estoque restaurado |

### Cenários adicionais

Implementados com base em análise de risco, priorizando situações capazes de gerar inconsistência de dados, estoque incorreto ou efeitos parciais após rejeição.

| ID | Cenário | Risco coberto |
|---|---|---|
| CT16 | Segundo carrinho para o mesmo usuário | Duplicidade e alteração indevida do estado existente |
| CT17 | Produto inexistente no carrinho | Falha de integridade entre carrinho e catálogo |
| CT18 | Quantidade superior ao estoque | Venda acima da disponibilidade |
| CT19 | Quantidade abaixo, igual e acima do estoque | Erros de fronteira (`>`, `>=`, `<`, `<=`) |
| CT20 | Produto duplicado no mesmo carrinho | Duplicidade, cálculo incorreto, alteração de estoque |
| CT-MULTI-01 | Carrinho com múltiplos produtos | Totalização e gerenciamento independente de estoque |
| CT-MULTI-02 | Um item sem estoque suficiente em solicitação múltipla | Atomicidade e prevenção de atualizações parciais |

### Cenários negativos

Cobrem validação de entrada e regras de negócio rejeitadas em todos os domínios.

| Grupo | Casos | Cobertura |
|---|---|---|
| Usuário | 6 | E-mail duplicado, campos obrigatórios ausentes, ID inexistente |
| Autenticação | 4 | Credenciais inválidas, campos ausentes |
| Produto | 8 | Campos obrigatórios, preço/quantidade negativos, nome duplicado, ID inexistente |
| Carrinho | 4 | Produtos ausentes, quantidade zero/negativa, ID de produto vazio |

### Condições de fronteira

Para um produto com estoque `5`:

```text
Solicitar 4 → sucesso
Solicitar 5 → sucesso
Solicitar 6 → rejeição
```

Nos casos rejeitados, o teste confirma que o estoque original permanece inalterado.

### Atomicidade

O cenário `CT-MULTI-02` usa dois produtos com estoques distintos e solicita quantidade indisponível em apenas um deles. A expectativa é que a operação seja rejeitada integralmente:

```text
nenhum carrinho criado
+ estoque do produto A inalterado
+ estoque do produto B inalterado
```

### Validações técnicas

- contratos JSON;
- consulta posterior dos recursos criados;
- validação de persistência;
- cálculo independente dos totais;
- validação individual de estoque;
- cleanup dos recursos;
- tratamento de falhas durante o cleanup;
- sanitização das evidências HTTP;
- testes de regressão do sanitizador;
- execução automática em CI;
- preservação de resultados Allure.

---

## Arquitetura

Organização por domínio, com responsabilidades técnicas compartilhadas em pacotes específicos.

```text
src/test/java/io/github/acarolinebcosta/serverest/
├── api/           ApiConfig, ApiMessages, CreateResponse, MessageResponse
├── auth/          LoginClient, LoginNegativeSteps, LoginRequest, LoginResponse, LoginService
├── cart/          CartAssertions, CartBuilder, CartCalculations, CartClient, CartNegativeSteps,
│                  CartRequest, CartResponse, CartService, CartSteps, CartStockAssertions,
│                  PurchaseSteps, ...
├── config/        EnvironmentConfig
├── context/       ScenarioContext
├── evidence/      EvidenceSanitizer, EvidenceSanitizerTest, SafeEvidenceFilter
├── lifecycle/     CleanupAssertions, CleanupService, ScenarioHooks
├── product/       CreatedProduct, ProductAliases, ProductBuilder, ProductClient, ProductData,
│                  ProductDataFactory, ProductNegativeSteps, ProductService, ProductSteps, ...
├── testdata/      DataGenerator
├── user/          UserClient, UserData, UserDataFactory, UserNegativeSteps, UserService,
│                  UserSteps, ...
├── validation/    ContractAssertions, ResponseAssertions
├── runner/        CucumberTest
└── CommonNegativeSteps.java
```

### Separação de responsabilidades

| Camada | Responsabilidade | Exemplos |
|---|---|---|
| Clients | Comunicação HTTP, sem regra de negócio ou assert | `UserClient`, `LoginClient`, `ProductClient`, `CartClient` |
| Services | Orquestram operações do domínio e atualizam o `ScenarioContext` | `UserService`, `ProductService`, `CartService`, `LoginService` |
| Assertions | Centralizam validações específicas | `ResponseAssertions`, `ContractAssertions`, `CartAssertions`, `CartStockAssertions`, `CleanupAssertions` |
| Steps | Traduzem Gherkin para services e assertions, sem regra de negócio | `*Steps`, `CommonNegativeSteps` |
| Context | Estado do cenário (`user`, `userId`, `token`, `products`, `cartId`, requisição ativa) | `ScenarioContext` |
| Lifecycle | Cleanup dos recursos e isolamento entre cenários | `CleanupService`, `ScenarioHooks` |

A instância do `ScenarioContext` é isolada por cenário pela injeção de dependências do Cucumber.

---

## BDD e contratos

Features em `src/test/resources/features/`:

```text
cadastro_usuario
fluxo_compra
fluxo_cancelamento
limites_estoque
multiplos_produtos
regras_carrinho
usuarios_negativos
autenticacao_negativa
produtos_negativos
carrinhos_negativos
```

Cenários escritos em português para leitura da regra de negócio; classes, métodos e identificadores Java em inglês.

Contratos JSON em `src/test/resources/schemas/`, cobrindo `login`, `produto` e `carrinhos`. Falhas de contrato são registradas no Allure somente após sanitização.

---

## Segurança das evidências

A automação evita publicar diretamente dados sensíveis: e-mail, senha, `Authorization`, Bearer token, JWT, `access_token`, `refresh_token`, `id_token`.

```text
REST Assured → SafeEvidenceFilter → EvidenceSanitizer → Allure
```

Campos sensíveis são substituídos por `[OMITIDO]`. Além de JSON estruturado, a sanitização cobre texto livre, mensagens de exceção e dados refletidos pela API.

`EvidenceSanitizerTest` possui 7 testes cobrindo objetos aninhados, arrays, variação de caixa em campos sensíveis, segredos previamente submetidos, texto livre, JWT/Bearer, resposta não JSON, JSON malformado, preservação do objeto original e proteção do `toString()` de DTOs sensíveis.

---

## Execução

### Pré-requisitos

- Java 21
- Maven
- Acesso à internet para comunicação com a ServeRest

Verificação:

```bash
java -version
mvn -version
```

### Configuração

Senhas não são armazenadas no código-fonte. A variável necessária é `SERVEREST_TEST_PASSWORD`. O `.env` local não é versionado; há um modelo em `.env.example`.

```bash
cp .env.example .env
# preencher SERVEREST_TEST_PASSWORD
set -a && source .env && set +a
```

### Passo inicial obrigatório

Antes da primeira execução (e sempre que `pom.xml`, dependências ou plugins mudarem), é necessário rodar:

```bash
mvn install
```

Esse passo baixa as dependências, compila o projeto e instala os artefatos no repositório local do Maven. Sem ele, a primeira execução de `mvn test` pode falhar por ausência de dependências resolvidas no repositório local.

### Comandos

| Objetivo | Comando |
|---|---|
| Preparar o projeto (obrigatório antes do primeiro teste) | `mvn install` |
| Suíte completa | `mvn clean test` |
| Somente sanitizador | `mvn -Dtest=EvidenceSanitizerTest test` |
| Somente Cucumber | `mvn -Dtest=CucumberTest test` |
| Outra URL | `mvn clean test -DbaseUrl=<url>` |
| Timeout customizado | `mvn clean test -Dapi.timeout.ms=<ms>` |
| Relatório Allure | `mvn allure:report` |
| Abrir relatório | `mvn allure:serve` |

Sequência completa na primeira execução:

```bash
cp .env.example .env
# preencher SERVEREST_TEST_PASSWORD
set -a && source .env && set +a
mvn install
mvn clean test
mvn allure:serve
```

---

## Relatórios

O relatório oficial da suíte é gerado pelo **Allure**:

```bash
mvn allure:serve
```

Os arquivos brutos ficam em `target/allure-results/` e são preservados no CI como artefato mesmo em caso de falha.

> A pasta `target/surefire-reports/` contém artefatos do Maven que **não refletem os cenários BDD** — o engine do Cucumber reporta via Allure, não via Surefire. Para análise de resultados, consulte apenas o Allure.

---

## Integração contínua

Workflow em `.github/workflows/api-tests.yml`, executado em `push` para `main`, pull requests para `main` e execução manual.

Ambiente: Ubuntu + Java 21 + Maven. A senha vem de GitHub Actions Secrets (`SERVEREST_TEST_PASSWORD`) e nunca é versionada.

Após a execução, `target/allure-results/` é preservado como artefato mesmo em caso de falha.

---

## Resultado atual

```text
Tests run: 41
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

Composição:

- **34 cenários BDD** de integração distribuídos em 10 features;
- **7 testes técnicos** do `EvidenceSanitizer`.

Tempo médio de execução local: **~4 min 30 s**.

---

## Investigação de incidente em produção

> "Às vezes, o cliente paga, mas o pedido não aparece na tela de 'Meus Pedidos'."

Contexto: sem acesso ao código-fonte, apenas produção, logs básicos e apoio dos times de Produto e Backend.

### Primeira ação

Correlacionar uma ocorrência real ponta a ponta com os identificadores disponíveis (`orderId`, `transactionId`, `correlationId`, horário, usuário afetado).

O primeiro objetivo é responder: **o pedido não foi persistido/atualizado corretamente, ou existe no backend e não está sendo retornado para a tela?** Essa separação reduz o espaço de investigação.

### Hipótese inicial

Por ser intermitente, a hipótese inicial é inconsistência entre a confirmação do pagamento e a atualização/leitura do pedido. Investigaria: falha de integração entre serviços, processamento assíncrono, timeout, atraso de consistência, perda de evento, problema de leitura, cache/indexação e filtros incorretos.

### Redução de incerteza

1. Obter uma ocorrência real com horário e identificadores.
2. Confirmar que a transação foi aprovada no serviço de pagamento.
3. Correlacionar `transactionId`/`orderId`/`correlationId` nos logs.
4. Verificar se a confirmação gerou a atualização esperada do pedido.
5. Consultar diretamente o backend ou o endpoint de "Meus Pedidos", quando disponível.
6. Comparar com uma transação semelhante concluída corretamente.

A partir disso, o problema se divide em dois grupos:

- **Pedido inexistente ou com estado incorreto** → falha na cadeia pagamento → confirmação → processamento → persistência.
- **Pedido existente no backend, mas ausente na tela** → problema de consulta, filtro, cache, indexação ou integração de leitura.

Evitaria tentar reproduzir repetidamente pela interface. Em problema intermitente, correlacionar uma ocorrência real tende a produzir evidência objetiva mais rápido.

---

## Escopo não coberto

A estratégia atual prioriza o fluxo solicitado e regras críticas de carrinho. Como evolução:

- acesso a recursos protegidos com token válido de usuário comum (permissões);
- token expirado;
- regras específicas de exclusão de recursos em uso (produto/usuário com carrinho ativo);
- limites adicionais de payload;
- testes de performance, carga, resiliência e segurança.

Respostas `5xx` não são provocadas artificialmente nesta implementação.

---

## Critérios adotados

- código simples antes de abstração;
- separação clara de responsabilidades;
- testes independentes;
- dados gerados dinamicamente;
- validação do estado persistido;
- cálculo independente do resultado esperado;
- cleanup após execução;
- segredos fora do código;
- evidências sanitizadas;
- cenários priorizados por risco.

Não foram adicionadas camadas como interfaces, factories ou service locators quando existia apenas uma implementação e nenhuma necessidade concreta de extensão.

---

## Documentação complementar

Estratégia, decisões de cobertura e critérios de priorização estão descritos neste próprio README. Para instruções operacionais, consulte as seções **Execução** e **Integração contínua**.

## Evidências

Screenshots do relatório Allure em `docs/evidence/screenshots/`:

- Dashboard com a execução completa;
- Detalhamento de um cenário E2E;
- Evidência HTTP sanitizada com credenciais omitidas.

Para gerar o relatório localmente: `mvn allure:serve`.