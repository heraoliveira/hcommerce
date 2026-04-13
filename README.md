# HCommerce

## English

A small console-based e-commerce application written in Java.

The project focuses on a compact but realistic flow: customer registration with ViaCEP lookup, product catalog management, active cart operations, checkout, and local JSON persistence.

---

### Project Goal

HCommerce was created to practice and consolidate:

- object-oriented design and separation of responsibilities
- domain modeling with constructor validation
- monetary calculations with `BigDecimal`
- custom exception handling
- JSON persistence with Jackson
- HTTP integration with ViaCEP
- interactive console flow
- unit testing with JUnit 5

---

### Current Status

The current implementation already provides:

- a working console menu
- product persistence
- order persistence
- current-customer persistence
- ViaCEP-based address lookup
- local validation in the console flow
- unit tests in `src/test/java`

When a saved current customer exists, the application loads it at startup. Products and orders are also loaded from local JSON files.

The system works with a single persisted current customer only. It does not implement a customer catalog or multi-customer management.

---

### Implemented Features

- **Customer registration**: collects name, email, and ZIP code, looks up the address through ViaCEP, creates the `Customer`, and persists it as the current customer.
- **Product registration**: creates products with validation for ID, name, description, and price.
- **Catalog listing and search**: lists all products, searches by ID, and searches by partial name.
- **Catalog removal rule**: removes a product from the catalog only when that product is not present in the active cart.
- **Cart operations**: adds products, merges repeated products by increasing quantity, updates quantity, removes items, and shows subtotal, discount, and total.
- **Cart pricing**: `CartPricing` calculates pricing from the current cart state using these thresholds:
  - subtotal less than or equal to `200.00`: no discount
  - subtotal greater than `200.00` and less than or equal to `500.00`: `10%`
  - subtotal greater than `500.00` and less than or equal to `1500.00`: `15%`
  - subtotal greater than `1500.00`: `20%`
- **Order creation**: creates completed orders from the cart using `Order.fromCart(...)`, with item snapshots, subtotal, discount amount, total, date/time, and status.
- **Financial consistency validation**: `Order` validates item list, subtotal, discount amount, total, and cross-field consistency, including when reading saved JSON.
- **JSON persistence**: products, orders, and the current customer are stored locally in JSON files.
- **ViaCEP integration boundary**: ViaCEP responses are first deserialized into `ViaCepResponse` and then converted into the domain model `Address`.
- **Console-side validation**: the console validates common inputs such as required text, positive integers, email addresses, and ZIP codes before invoking the application flow.
- **Controlled error handling**: the main loop handles validation, cart, product, ZIP, integration, and persistence/state errors without terminating the program abruptly in normal use.

---

### Current Console Flow

The current main menu is:

0. Exit
1. Register customer
2. Register product
3. List products
4. Add product to cart
5. Update product quantity
6. Remove product from cart
7. View cart
8. List saved orders
9. Finalize order
10. Search products
11. Remove product from catalog

Important behavior notes:

- the menu shows the current customer loaded from local persistence when available
- the menu shows the total number of units in the cart, not the number of distinct products
- common invalid inputs are re-prompted locally in the console flow
- flows that require selecting an item by ID allow canceling the current operation with `0`
- a product currently present in the active cart cannot be removed from the catalog
- checkout requires a registered customer and a non-empty cart
- a successful checkout saves the order and resets the active cart

---

### Current Architecture

The project uses small packages aligned with the current application size:

- `console`: terminal interaction, menu flow, input handling, and output formatting
- `models`: domain entities and domain rules such as cart state, pricing, orders, and validation
- `repository`: JSON persistence for products, orders, and the single current customer
- `service`: application-level orchestration and integration services
- `infra`: low-level HTTP boundary abstractions and implementations
- `util`: shared utilities for JSON, formatting, validation, debug mode, and persistence paths
- `exception`: custom runtime exceptions used across the application

`Main` bootstraps the concrete repositories and starts `ConsoleApplication`.

---

### JSON Persistence

Persistence is centralized by `util/PersistencePaths.java`, which resolves the data directory under the user home folder.

Current files:

- `~/.hcommerce/data/products.json`
- `~/.hcommerce/data/orders.json`
- `~/.hcommerce/data/customer.json`

Behavior:

- `ProductRepository` and `OrderRepository` extend `AbstractJsonRepository`
- products and orders are loaded into memory during repository initialization
- missing repository files are created automatically when needed
- empty `products.json` is normalized to `[]`
- `CurrentCustomerRepository` loads the single current customer when `customer.json` exists and contains data
- if `customer.json` is absent or empty, the application starts with no current customer
- saving a new current customer overwrites `customer.json`

Important note:

- the application persists one current customer, not a list of customers

---

### ViaCEP Integration

ViaCEP integration is part of the customer registration flow.

- `AddressLookupService` defines the lookup contract and still returns the domain model `Address`
- `ViaCepService` implements that contract
- `HttpFetcher` defines the HTTP boundary used by the service
- `HttpClientFetcher` is the default implementation based on `java.net.http.HttpClient`
- `ViaCepResponse` represents the external ViaCEP payload
- `ViaCepService` converts `ViaCepResponse` into `Address`
- `ZipValidation` validates and normalizes ZIP code input
- `Address` remains the domain model and keeps domain validation for required address fields
- nonexistent ZIP codes, malformed responses, timeouts, unavailable service, and invalid external field values are handled through controlled exceptions

---

### Key Classes and Responsibilities

- `Main`: application bootstrap and top-level fatal error reporting
- `ConsoleApplication`: main menu loop and centralized exception handling for the interactive session
- `ConsoleInput`: console-side parsing and basic input validation
- `ConsolePrinter`: menu, catalog, cart, customer, and order output formatting
- `CustomerConsoleHandler`, `ProductConsoleHandler`, `CartConsoleHandler`, `OrderConsoleHandler`: console interaction handlers for each user flow
- `CustomerService`, `ProductService`, `OrderService`: application services that coordinate use cases and repository/domain interaction
- `Cart`: active cart state and item operations
- `CartPricing`: discount and total calculation rules
- `Order`: order creation from the cart plus financial consistency validation
- `AbstractJsonRepository`: shared JSON persistence behavior for collection-based repositories
- `ProductRepository`, `OrderRepository`, `CurrentCustomerRepository`: local persistence components
- `AddressLookupService`, `ViaCepService`, `ViaCepResponse`: ViaCEP lookup contract, implementation, and transport DTO
- `HttpFetcher`, `HttpClientFetcher`: HTTP abstraction and default implementation
- `PersistencePaths`: centralized file-location resolution
- `JsonUtil`: shared Jackson `ObjectMapper`
- `MoneyFormatter`: Brazilian currency presentation
- `DebugMode`: optional debug switch for stack traces in unexpected-error paths

---

### Technology Stack

- **Java 17**
- **Maven**
- **Jackson**
- **JUnit 5**
- **java.net.http.HttpClient**

---

### Project Structure

```text
.
|-- pom.xml
|-- README.md
`-- src
    |-- main
    |   `-- java
    |       `-- br/com/heraoliveira/hcommerce
    |           |-- Main.java
    |           |-- console
    |           |   |-- CartConsoleHandler.java
    |           |   |-- ConsoleApplication.java
    |           |   |-- ConsoleInput.java
    |           |   |-- ConsolePrinter.java
    |           |   |-- CustomerConsoleHandler.java
    |           |   |-- OrderConsoleHandler.java
    |           |   `-- ProductConsoleHandler.java
    |           |-- exception
    |           |   |-- ExternalServiceException.java
    |           |   |-- InvalidCartException.java
    |           |   |-- InvalidCepException.java
    |           |   |-- InvalidDataException.java
    |           |   |-- ProductNotFoundException.java
    |           |   `-- ProductRemovalNotAllowedException.java
    |           |-- infra
    |           |   |-- HttpClientFetcher.java
    |           |   `-- HttpFetcher.java
    |           |-- models
    |           |   |-- Address.java
    |           |   |-- Cart.java
    |           |   |-- CartItem.java
    |           |   |-- CartPricing.java
    |           |   |-- Customer.java
    |           |   |-- Order.java
    |           |   |-- OrderItem.java
    |           |   |-- OrderStatus.java
    |           |   `-- Product.java
    |           |-- repository
    |           |   |-- AbstractJsonRepository.java
    |           |   |-- CurrentCustomerRepository.java
    |           |   |-- OrderRepository.java
    |           |   `-- ProductRepository.java
    |           |-- service
    |           |   |-- AddressLookupService.java
    |           |   |-- CustomerService.java
    |           |   |-- OrderService.java
    |           |   |-- ProductService.java
    |           |   |-- ViaCepResponse.java
    |           |   `-- ViaCepService.java
    |           `-- util
    |               |-- DebugMode.java
    |               |-- EmailValidation.java
    |               |-- JsonUtil.java
    |               |-- MoneyFormatter.java
    |               |-- PersistencePaths.java
    |               `-- ZipValidation.java
    `-- test
        `-- java
            `-- br/com/heraoliveira/hcommerce
                |-- models
                |   |-- CartTest.java
                |   |-- CustomerTest.java
                |   |-- OrderTest.java
                |   `-- ProductTest.java
                |-- repository
                |   `-- RepositoryPathInjectionTest.java
                |-- service
                |   |-- ProductServiceTest.java
                |   `-- ViaCepServiceTest.java
                `-- util
                    |-- MoneyFormatterTest.java
                    |-- PersistencePathsTest.java
                    `-- ZipValidationTest.java
```

---

### Tests

Unit tests already exist in `src/test/java`.

Current coverage includes:

- cart pricing thresholds, quantity rules, merges, and cart-item removal/update errors
- customer normalization and validation
- product normalization and validation
- order creation from cart and financial consistency checks
- repository persistence using injected paths
- absent, empty, and invalid JSON handling in persistence
- current-customer persistence and startup loading behavior
- catalog-removal blocking when a product is present in the active cart
- ViaCEP parsing, nonexistent ZIP handling, URL normalization, runtime failure wrapping, and invalid external field propagation
- Brazilian money formatting
- user-home persistence path resolution
- ZIP code normalization and invalid-format rejection

---

### Build and Run

From the project root:

```bash
mvn clean test
mvn exec:java
```

Optional debug mode:

- system property: `-Dhcommerce.debug=true`
- environment variable: `HCOMMERCE_DEBUG=true`

When debug mode is enabled, unexpected top-level errors print stack traces in addition to the normal console message.

---

# HCommerce

## Português

Uma pequena aplicação de e-commerce em console escrita em Java.

O projeto foca em um fluxo compacto, mas realista: cadastro de cliente com consulta ao ViaCEP, gerenciamento de catálogo de produtos, operações sobre o carrinho ativo, checkout e persistência local em JSON.

---

### Objetivo do projeto

O HCommerce foi criado para praticar e consolidar:

- orientação a objetos e separação de responsabilidades
- modelagem de domínio com validação em construtores
- cálculos monetários com `BigDecimal`
- tratamento de exceções customizadas
- persistência em JSON com Jackson
- integração HTTP com ViaCEP
- fluxo interativo em console
- testes unitários com JUnit 5

---

### Estado atual

A implementação atual já oferece:

- um menu de console funcional
- persistência de produtos
- persistência de pedidos
- persistência do cliente atual
- busca de endereço via ViaCEP
- validação local no fluxo de console
- testes unitários em `src/test/java`

Quando existe um cliente atual salvo, a aplicação o carrega na inicialização. Produtos e pedidos também são carregados a partir de arquivos JSON locais.

O sistema trabalha com apenas um cliente atual persistido. Ele não implementa catálogo de clientes nem gerenciamento multiusuário.

---

### Funcionalidades implementadas

- **Cadastro de cliente**: coleta nome, e-mail e CEP, consulta o endereço no ViaCEP, cria o `Customer` e o persiste como cliente atual.
- **Cadastro de produto**: cria produtos com validação de ID, nome, descrição e preço.
- **Listagem e busca no catálogo**: lista todos os produtos, busca por ID e busca por trecho do nome.
- **Regra de remoção do catálogo**: remove um produto do catálogo somente quando ele não está presente no carrinho ativo.
- **Operações de carrinho**: adiciona produtos, consolida produtos repetidos somando quantidade, atualiza quantidade, remove itens e exibe subtotal, desconto e total.
- **Precificação do carrinho**: `CartPricing` calcula os valores a partir do estado atual do carrinho usando estes limites:
  - subtotal menor ou igual a `200.00`: sem desconto
  - subtotal maior que `200.00` e menor ou igual a `500.00`: `10%`
  - subtotal maior que `500.00` e menor ou igual a `1500.00`: `15%`
  - subtotal maior que `1500.00`: `20%`
- **Criação de pedido**: cria pedidos concluídos a partir do carrinho com `Order.fromCart(...)`, incluindo snapshot dos itens, subtotal, valor do desconto, total, data/hora e status.
- **Validação de consistência financeira**: `Order` valida lista de itens, subtotal, desconto, total e consistência entre campos, inclusive ao ler JSON salvo.
- **Persistência em JSON**: produtos, pedidos e o cliente atual são armazenados localmente em arquivos JSON.
- **Separação da integração ViaCEP**: as respostas do ViaCEP são desserializadas primeiro em `ViaCepResponse` e só depois convertidas para o modelo de domínio `Address`.
- **Validação no console**: o console valida entradas comuns como texto obrigatório, inteiros positivos, e-mail e CEP antes de acionar o fluxo da aplicação.
- **Tratamento controlado de erros**: o loop principal trata erros de validação, carrinho, produto, CEP, integração e persistência/estado sem encerrar o programa abruptamente no uso normal.

---

### Fluxo atual do console

O menu principal atual é:

0. Sair
1. Cadastrar cliente
2. Cadastrar produto
3. Listar produtos
4. Adicionar produto ao carrinho
5. Atualizar quantidade do produto
6. Remover produto do carrinho
7. Ver carrinho
8. Listar pedidos salvos
9. Finalizar pedido
10. Buscar produtos
11. Remover produto do catálogo

Observações importantes de comportamento:

- o menu mostra o cliente atual carregado da persistência local quando disponível
- o menu mostra o número total de unidades no carrinho, e não a quantidade de produtos distintos
- entradas inválidas comuns são pedidas novamente localmente no fluxo de console
- fluxos que exigem seleção de item por ID permitem cancelar a operação atual com `0`
- um produto atualmente presente no carrinho ativo não pode ser removido do catálogo
- o checkout exige um cliente cadastrado e um carrinho não vazio
- um checkout bem-sucedido salva o pedido e reinicia o carrinho ativo

---

### Arquitetura atual

O projeto usa pacotes pequenos, alinhados ao tamanho atual da aplicação:

- `console`: interação de terminal, fluxo de menu, leitura de entrada e formatação de saída
- `models`: entidades de domínio e regras de domínio, como estado do carrinho, precificação, pedidos e validação
- `repository`: persistência em JSON de produtos, pedidos e do cliente atual único
- `service`: orquestração em nível de aplicação e serviços de integração
- `infra`: abstrações e implementações de baixo nível para HTTP
- `util`: utilitários compartilhados para JSON, formatação, validação, modo debug e caminhos de persistência
- `exception`: exceções runtime customizadas usadas na aplicação

`Main` faz o bootstrap dos repositórios concretos e inicia `ConsoleApplication`.

---

### Persistência em JSON

A persistência é centralizada por `util/PersistencePaths.java`, que resolve o diretório de dados dentro da pasta home do usuário.

Arquivos atuais:

- `~/.hcommerce/data/products.json`
- `~/.hcommerce/data/orders.json`
- `~/.hcommerce/data/customer.json`

Comportamento:

- `ProductRepository` e `OrderRepository` estendem `AbstractJsonRepository`
- produtos e pedidos são carregados em memória durante a inicialização dos repositórios
- arquivos ausentes dos repositórios são criados automaticamente quando necessário
- um `products.json` vazio é normalizado para `[]`
- `CurrentCustomerRepository` carrega o cliente atual único quando `customer.json` existe e contém dados
- se `customer.json` estiver ausente ou vazio, a aplicação inicia sem cliente atual
- salvar um novo cliente atual sobrescreve `customer.json`

Observação importante:

- a aplicação persiste um cliente atual, e não uma lista de clientes

---

### Integração com ViaCEP

A integração com ViaCEP faz parte do fluxo de cadastro de cliente.

- `AddressLookupService` define o contrato de busca e continua retornando o modelo de domínio `Address`
- `ViaCepService` implementa esse contrato
- `HttpFetcher` define a fronteira HTTP usada pelo serviço
- `HttpClientFetcher` é a implementação padrão baseada em `java.net.http.HttpClient`
- `ViaCepResponse` representa o payload externo do ViaCEP
- `ViaCepService` converte `ViaCepResponse` em `Address`
- `ZipValidation` valida e normaliza a entrada de CEP
- `Address` continua sendo o modelo de domínio e mantém a validação de campos obrigatórios do endereço
- CEP inexistente, resposta malformada, timeout, indisponibilidade do serviço e valores externos inválidos são tratados por exceções controladas

---

### Classes-chave e responsabilidades

- `Main`: bootstrap da aplicação e tratamento de erros fatais no topo
- `ConsoleApplication`: loop principal do menu e tratamento centralizado de exceções da sessão interativa
- `ConsoleInput`: parsing e validação básica de entrada no console
- `ConsolePrinter`: formatação de saída de menu, catálogo, carrinho, cliente e pedido
- `CustomerConsoleHandler`, `ProductConsoleHandler`, `CartConsoleHandler`, `OrderConsoleHandler`: handlers de interação para cada fluxo do usuário
- `CustomerService`, `ProductService`, `OrderService`: serviços de aplicação que coordenam casos de uso e a interação entre repositórios e domínio
- `Cart`: estado do carrinho ativo e operações sobre itens
- `CartPricing`: regras de cálculo de desconto e total
- `Order`: criação de pedido a partir do carrinho e validação de consistência financeira
- `AbstractJsonRepository`: comportamento compartilhado de persistência JSON para repositórios baseados em coleção
- `ProductRepository`, `OrderRepository`, `CurrentCustomerRepository`: componentes de persistência local
- `AddressLookupService`, `ViaCepService`, `ViaCepResponse`: contrato de consulta ao ViaCEP, implementação e DTO de transporte
- `HttpFetcher`, `HttpClientFetcher`: abstração HTTP e implementação padrão
- `PersistencePaths`: resolução centralizada dos locais de arquivo
- `JsonUtil`: `ObjectMapper` compartilhado do Jackson
- `MoneyFormatter`: apresentação monetária brasileira
- `DebugMode`: chave opcional de debug para imprimir stack traces em caminhos de erro inesperado

---

### Stack tecnológico

- **Java 17**
- **Maven**
- **Jackson**
- **JUnit 5**
- **java.net.http.HttpClient**

---

### Estrutura do projeto

```text
.
|-- pom.xml
|-- README.md
`-- src
    |-- main
    |   `-- java
    |       `-- br/com/heraoliveira/hcommerce
    |           |-- Main.java
    |           |-- console
    |           |   |-- CartConsoleHandler.java
    |           |   |-- ConsoleApplication.java
    |           |   |-- ConsoleInput.java
    |           |   |-- ConsolePrinter.java
    |           |   |-- CustomerConsoleHandler.java
    |           |   |-- OrderConsoleHandler.java
    |           |   `-- ProductConsoleHandler.java
    |           |-- exception
    |           |   |-- ExternalServiceException.java
    |           |   |-- InvalidCartException.java
    |           |   |-- InvalidCepException.java
    |           |   |-- InvalidDataException.java
    |           |   |-- ProductNotFoundException.java
    |           |   `-- ProductRemovalNotAllowedException.java
    |           |-- infra
    |           |   |-- HttpClientFetcher.java
    |           |   `-- HttpFetcher.java
    |           |-- models
    |           |   |-- Address.java
    |           |   |-- Cart.java
    |           |   |-- CartItem.java
    |           |   |-- CartPricing.java
    |           |   |-- Customer.java
    |           |   |-- Order.java
    |           |   |-- OrderItem.java
    |           |   |-- OrderStatus.java
    |           |   `-- Product.java
    |           |-- repository
    |           |   |-- AbstractJsonRepository.java
    |           |   |-- CurrentCustomerRepository.java
    |           |   |-- OrderRepository.java
    |           |   `-- ProductRepository.java
    |           |-- service
    |           |   |-- AddressLookupService.java
    |           |   |-- CustomerService.java
    |           |   |-- OrderService.java
    |           |   |-- ProductService.java
    |           |   |-- ViaCepResponse.java
    |           |   `-- ViaCepService.java
    |           `-- util
    |               |-- DebugMode.java
    |               |-- EmailValidation.java
    |               |-- JsonUtil.java
    |               |-- MoneyFormatter.java
    |               |-- PersistencePaths.java
    |               `-- ZipValidation.java
    `-- test
        `-- java
            `-- br/com/heraoliveira/hcommerce
                |-- models
                |   |-- CartTest.java
                |   |-- CustomerTest.java
                |   |-- OrderTest.java
                |   `-- ProductTest.java
                |-- repository
                |   `-- RepositoryPathInjectionTest.java
                |-- service
                |   |-- ProductServiceTest.java
                |   `-- ViaCepServiceTest.java
                `-- util
                    |-- MoneyFormatterTest.java
                    |-- PersistencePathsTest.java
                    `-- ZipValidationTest.java
```

---

### Testes

Já existem testes unitários em `src/test/java`.

A cobertura atual inclui:

- limites de precificação do carrinho, regras de quantidade, consolidação de itens e erros de remoção/atualização
- normalização e validação de cliente
- normalização e validação de produto
- criação de pedido a partir do carrinho e checagens de consistência financeira
- persistência em repositórios com caminhos injetados
- tratamento de JSON ausente, vazio e inválido na persistência
- persistência do cliente atual e comportamento de carregamento na inicialização
- bloqueio de remoção do catálogo quando o produto está no carrinho ativo
- parsing do ViaCEP, tratamento de CEP inexistente, normalização de URL, encapsulamento de falhas runtime e propagação de campos externos inválidos
- formatação monetária brasileira
- resolução de caminhos de persistência na pasta home do usuário
- normalização de CEP e rejeição de formato inválido

---

### Execução

Na raiz do projeto:

```bash
mvn clean test
mvn exec:java
```

Modo de debug opcional:

- property de sistema: `-Dhcommerce.debug=true`
- variável de ambiente: `HCOMMERCE_DEBUG=true`

Quando o modo debug está habilitado, erros inesperados no topo imprimem stack trace além da mensagem normal no console.

---
