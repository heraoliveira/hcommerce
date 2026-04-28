# HCommerce

<details>
<summary><strong>English</strong></summary>

### Overview

HCommerce is a small console-based e-commerce application written in Java 17.

It was built as a study and portfolio project to practice core Java concepts with a realistic end-to-end flow: current-customer registration, product catalog management, active-cart operations, order checkout, ViaCEP integration, and local JSON persistence.

Current project version: `0.1.0`.

### What the project currently does

The application already supports the full MVP flow:

- register the current customer
- look up the customer address through ViaCEP
- register products
- search products by ID or partial name
- list the product catalog
- remove products from the catalog with active-cart protection
- add products to the active cart
- update cart item quantity
- remove items from the active cart
- view the current cart with pricing details
- finalize an order from the current cart
- list saved orders

### Main menu

The current main menu is:

1. Register customer
2. Register product
3. Remove product from catalog
4. Search products
5. List products
6. Add product to cart
7. Update cart product quantity
8. Remove product from cart
9. View cart
10. Finalize order
11. List saved orders
0. Exit

### Important behavior notes

- The application loads the current customer from local persistence on startup when `customer.json` exists and contains data.
- The active cart exists only in memory during the current execution.
- The menu shows the total number of units in the cart, not the number of distinct products.
- Product ID is validated before the quantity prompt in the "Add product to cart" flow.
- Cart item ID is validated before the new quantity prompt in the "Update cart product quantity" flow.
- A product that is currently present in the active cart cannot be removed from the catalog.
- Checkout requires a registered current customer and a non-empty cart.
- A successful checkout persists the order and resets the in-memory cart.

### Technology stack

- Java 17
- Maven
- Jackson
- JUnit 5
- `java.net.http.HttpClient`

### Current architecture

The project is organized into small packages that match the current size of the application:

- `console`: terminal interaction, menu routing, input reading, and output formatting
- `service`: application-level use-case orchestration
- `model`: domain entities, value rules, pricing, and order/cart invariants
- `repository`: local JSON persistence
- `infra`: technical integration boundary for HTTP
- `util`: reusable pure utilities such as validation, formatting, JSON mapper, debug mode, and persistence paths
- `exception`: semantic runtime exceptions used across the project

### Package structure

```text
src/main/java/br/com/heraoliveira/hcommerce/
├── Main.java
├── console/
├── exception/
├── infra/
├── model/
├── repository/
├── service/
└── util/

src/test/java/br/com/heraoliveira/hcommerce/
├── console/
├── model/
├── repository/
├── service/
└── util/
```

### Key components

- `Main`: bootstraps the application and reports fatal startup errors
- `ConsoleApplication`: owns the main menu loop and top-level exception handling during the interactive session
- `ConsoleInput`: handles local console parsing and input validation
- `ConsolePrinter`: formats the menu, catalog, cart, customer, and order output
- `CustomerConsoleHandler`, `ProductConsoleHandler`, `CartConsoleHandler`, `OrderConsoleHandler`: user-flow handlers in the console layer
- `CustomerService`: creates and persists the current customer
- `ProductService`: coordinates catalog use cases
- `CartService`: coordinates active-cart operations that depend on catalog state
- `OrderService`: finalizes and persists orders, using semantic failure results for expected business preconditions
- `ProductRepository`, `OrderRepository`, `CurrentCustomerRepository`: JSON persistence components
- `AbstractJsonRepository`: shared persistence behavior for collection-based repositories
- `ViaCepService`: address lookup service backed by ViaCEP
- `ViaCepResponse`: transport DTO for the external ViaCEP payload
- `HttpFetcher` and `HttpClientFetcher`: HTTP abstraction and default implementation

### Domain and pricing rules

- `Cart` stores the active cart state in memory.
- `CartPricing` computes subtotal, discount percentage, discount amount, and total.
- Current discount thresholds are:
  - subtotal `<= 200.00`: `0%`
  - subtotal `> 200.00` and `<= 500.00`: `10%`
  - subtotal `> 500.00` and `<= 1500.00`: `15%`
  - subtotal `> 1500.00`: `20%`
- `Order.fromCart(...)` creates an order snapshot from the current cart.
- Financial consistency is validated in the order model itself.

### Local persistence

Persistence is file-based and resolved through `PersistencePaths`.

Current files:

- `~/.hcommerce/data/products.json`
- `~/.hcommerce/data/orders.json`
- `~/.hcommerce/data/customer.json`

Current behavior:

- products and orders are loaded through `AbstractJsonRepository`
- the current customer is loaded through `CurrentCustomerRepository`
- missing files and directories are created when needed
- empty `products.json` is normalized to `[]`
- empty or absent `customer.json` means "no current customer loaded"
- invalid JSON in repository files fails predictably with `IllegalStateException`

### ViaCEP integration

ViaCEP is part of the customer registration flow.

- `AddressLookupService` defines the lookup contract
- `ViaCepService` implements that contract
- ZIP code input is normalized before the HTTP request URL is built
- external JSON is deserialized into `ViaCepResponse`
- `ViaCepResponse` is converted explicitly into the domain model `Address`
- invalid ZIP codes, nonexistent ZIP codes, malformed payloads, and unavailable service scenarios are mapped to semantic exceptions

### Running locally

From the project root:

```bash
mvn clean test
mvn exec:java
```

Optional debug mode:

- JVM property: `-Dhcommerce.debug=true`
- environment variable: `HCOMMERCE_DEBUG=true`

When debug mode is enabled, unexpected top-level errors print a stack trace in addition to the standard console message.

### Test coverage

Unit tests already exist in `src/test/java`.

The current suite covers:

- console flow checks for menu order and early ID validation
- cart pricing thresholds and cart quantity behavior
- customer validation
- product validation
- order creation and financial consistency
- `CartService`, `ProductService`, and `OrderService` edge cases
- current-customer persistence
- product and order repository persistence
- absent, empty, and invalid JSON scenarios
- ViaCEP parsing and integration failure handling
- persistence path resolution
- money formatting
- ZIP code validation

Run the full suite with:

```bash
mvn test
```

### Current limitations

- The application persists only one current customer.
- The active cart is not persisted between executions.
- There is no stock control.
- There is no payment flow.
- There is no authentication or multi-user support.
- Orders are local JSON records only.
- The project is a console application only; it does not expose a REST API or graphical UI.

### Possible next steps

Possible future improvements, outside the current MVP:

- persist the active cart between runs
- introduce stock availability rules
- expand order lifecycle states beyond the current simplified flow
- add automated integration tests for console/session flows

</details>

---

<details>
<summary><strong>Português</strong></summary>

### Visão geral

HCommerce é uma pequena aplicação de e-commerce em console escrita em Java 17.

Ela foi construída como projeto de estudo e portfólio para praticar conceitos centrais de Java com um fluxo realista de ponta a ponta: cadastro do cliente atual, gerenciamento de catálogo de produtos, operações sobre o carrinho ativo, finalização de pedido, integração com ViaCEP e persistência local em JSON.

Versão atual do projeto: `0.1.0`.

### O que o projeto faz hoje

A aplicação já suporta o fluxo completo do MVP:

- cadastrar o cliente atual
- consultar o endereço do cliente via ViaCEP
- cadastrar produtos
- buscar produtos por ID ou trecho do nome
- listar o catálogo de produtos
- remover produtos do catálogo com proteção contra remoção quando estão no carrinho ativo
- adicionar produtos ao carrinho ativo
- atualizar a quantidade de itens do carrinho
- remover itens do carrinho ativo
- visualizar o carrinho atual com detalhes de preço
- finalizar um pedido a partir do carrinho atual
- listar pedidos salvos

### Menu principal

O menu principal atual é:

1. Cadastrar cliente
2. Cadastrar produto
3. Remover produto do catálogo
4. Buscar produtos
5. Listar produtos
6. Adicionar produto ao carrinho
7. Atualizar quantidade do produto no carrinho
8. Remover produto do carrinho
9. Ver carrinho
10. Finalizar pedido
11. Listar pedidos salvos
0. Sair

### Observações importantes de comportamento

- A aplicação carrega o cliente atual da persistência local na inicialização quando `customer.json` existe e contém dados.
- O carrinho ativo existe apenas em memória durante a execução atual.
- O menu mostra o total de unidades no carrinho, e não a quantidade de produtos distintos.
- O ID do produto é validado antes do prompt de quantidade no fluxo "Adicionar produto ao carrinho".
- O ID do item do carrinho é validado antes do prompt da nova quantidade no fluxo "Atualizar quantidade do produto no carrinho".
- Um produto que está presente no carrinho ativo não pode ser removido do catálogo.
- O checkout exige um cliente atual cadastrado e um carrinho não vazio.
- Um checkout bem-sucedido persiste o pedido e reinicia o carrinho em memória.

### Stack tecnológica

- Java 17
- Maven
- Jackson
- JUnit 5
- `java.net.http.HttpClient`

### Arquitetura atual

O projeto está organizado em pacotes pequenos, compatíveis com o tamanho atual da aplicação:

- `console`: interação com terminal, roteamento do menu, leitura de entrada e formatação de saída
- `service`: orquestração dos casos de uso em nível de aplicação
- `model`: entidades de domínio, regras de valor, precificação e invariantes de pedido/carrinho
- `repository`: persistência local em JSON
- `infra`: fronteira técnica de integração HTTP
- `util`: utilitários puros e reutilizáveis, como validação, formatação, mapper JSON, modo debug e caminhos de persistência
- `exception`: exceções semânticas em runtime usadas no projeto

### Estrutura de pacotes

```text
src/main/java/br/com/heraoliveira/hcommerce/
├── Main.java
├── console/
├── exception/
├── infra/
├── model/
├── repository/
├── service/
└── util/

src/test/java/br/com/heraoliveira/hcommerce/
├── console/
├── model/
├── repository/
├── service/
└── util/
```

### Componentes principais

- `Main`: faz o bootstrap da aplicação e reporta erros fatais de inicialização
- `ConsoleApplication`: controla o loop principal do menu e o tratamento de exceções no nível da sessão interativa
- `ConsoleInput`: cuida do parsing local e da validação de entrada no console
- `ConsolePrinter`: formata menu, catálogo, carrinho, cliente e pedidos
- `CustomerConsoleHandler`, `ProductConsoleHandler`, `CartConsoleHandler`, `OrderConsoleHandler`: handlers dos fluxos de usuário na camada de console
- `CustomerService`: cria e persiste o cliente atual
- `ProductService`: coordena os casos de uso do catálogo
- `CartService`: coordena as operações do carrinho ativo que dependem do estado do catálogo
- `OrderService`: finaliza e persiste pedidos, usando retornos semânticos para pré-condições de negócio esperadas
- `ProductRepository`, `OrderRepository`, `CurrentCustomerRepository`: componentes de persistência em JSON
- `AbstractJsonRepository`: comportamento compartilhado de persistência para repositórios baseados em coleção
- `ViaCepService`: serviço de consulta de endereço apoiado no ViaCEP
- `ViaCepResponse`: DTO de transporte do payload externo do ViaCEP
- `HttpFetcher` e `HttpClientFetcher`: abstração HTTP e implementação padrão

### Regras de domínio e precificação

- `Cart` armazena o estado do carrinho ativo em memória.
- `CartPricing` calcula subtotal, percentual de desconto, valor de desconto e total.
- Os limites atuais de desconto são:
  - subtotal `<= 200.00`: `0%`
  - subtotal `> 200.00` e `<= 500.00`: `10%`
  - subtotal `> 500.00` e `<= 1500.00`: `15%`
  - subtotal `> 1500.00`: `20%`
- `Order.fromCart(...)` cria um snapshot do pedido a partir do carrinho atual.
- A consistência financeira é validada no próprio modelo de pedido.

### Persistência local

A persistência é baseada em arquivos e resolvida por `PersistencePaths`.

Arquivos atuais:

- `~/.hcommerce/data/products.json`
- `~/.hcommerce/data/orders.json`
- `~/.hcommerce/data/customer.json`

Comportamento atual:

- produtos e pedidos são carregados por `AbstractJsonRepository`
- o cliente atual é carregado por `CurrentCustomerRepository`
- arquivos e diretórios ausentes são criados quando necessário
- `products.json` vazio é normalizado para `[]`
- `customer.json` vazio ou ausente significa "nenhum cliente atual carregado"
- JSON inválido em arquivos de repositório falha de forma previsível com `IllegalStateException`

### Integração com ViaCEP

O ViaCEP faz parte do fluxo de cadastro do cliente.

- `AddressLookupService` define o contrato de busca
- `ViaCepService` implementa esse contrato
- a entrada de CEP é normalizada antes da montagem da URL da requisição HTTP
- o JSON externo é desserializado para `ViaCepResponse`
- `ViaCepResponse` é convertido explicitamente para o modelo de domínio `Address`
- cenários de CEP inválido, CEP inexistente, payload malformado e indisponibilidade do serviço são mapeados para exceções semânticas

### Como executar localmente

Na raiz do projeto:

```bash
mvn clean test
mvn exec:java
```

Modo de debug opcional:

- propriedade da JVM: `-Dhcommerce.debug=true`
- variável de ambiente: `HCOMMERCE_DEBUG=true`

Quando o modo debug está habilitado, erros inesperados no topo imprimem stack trace além da mensagem padrão do console.

### Cobertura de testes

Já existem testes unitários em `src/test/java`.

A suíte atual cobre:

- verificações de fluxo de console para ordem do menu e validação antecipada de ID
- limites de precificação do carrinho e comportamento de quantidade
- validação de cliente
- validação de produto
- criação de pedido e consistência financeira
- casos de borda de `CartService`, `ProductService` e `OrderService`
- persistência do cliente atual
- persistência de produtos e pedidos
- cenários de JSON ausente, vazio e inválido
- parsing do ViaCEP e tratamento de falhas de integração
- resolução de caminhos de persistência
- formatação monetária
- validação de CEP

Execute toda a suíte com:

```bash
mvn test
```

### Limitações atuais

- A aplicação persiste apenas um cliente atual.
- O carrinho ativo não é persistido entre execuções.
- Não existe controle de estoque.
- Não existe fluxo de pagamento.
- Não existe autenticação nem suporte multiusuário.
- Os pedidos são apenas registros locais em JSON.
- O projeto é somente uma aplicação de console; ele não expõe API REST nem interface gráfica.

### Próximos passos possíveis

Melhorias futuras possíveis, fora do MVP atual:

- persistir o carrinho ativo entre execuções
- introduzir regras de disponibilidade de estoque
- expandir o ciclo de vida de pedidos além do fluxo simplificado atual
- adicionar testes de integração automatizados para fluxos completos de sessão/console

</details>
