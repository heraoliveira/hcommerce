# HCommerce

Projeto em desenvolvimento para praticar fundamentos de Java com um mini sistema de compras no console.

A proposta do repositório é consolidar conceitos de orientação a objetos, modelagem de domínio, coleções, validações, tratamento de exceções, integração com API externa e preparação para persistência em JSON.

---

## Objetivo

Construir um sistema simples de e-commerce em terminal, com foco em:

- modelagem de entidades e responsabilidades
- encapsulamento e validações no construtor
- uso de `BigDecimal` para valores monetários
- composição entre classes do domínio
- tratamento de exceções personalizadas
- integração com API externa (ViaCEP)
- organização em camadas
- preparação para persistência em JSON/arquivo

---

## Estado atual do projeto

Neste momento, o projeto já possui uma base sólida do domínio, regras centrais do carrinho, integração funcional com ViaCEP, geração de pedidos com snapshot dos itens e repositórios em memória para produtos e pedidos.

### Já implementado

#### Modelos de domínio

- `Product`
  - recebe ID por construtor
  - nome, descrição e preço
  - validações no construtor
  - alteração de preço com validação
  - `toString`, `equals` e `hashCode`

- `Customer`
  - nome, e-mail e endereço
  - validação de nome
  - validação de e-mail com classe utilitária
  - exigência de endereço válido no construtor

- `Address`
  - implementado como `record`
  - mapeado com Jackson para a resposta da ViaCEP
  - normalização do CEP
  - `street` e `neighborhood` opcionais
  - `city` e `state` obrigatórios

- `CartItem`
  - associação entre produto e quantidade
  - validação de item inválido
  - cálculo de subtotal
  - incremento e atualização de quantidade
  - igualdade baseada no produto

- `Cart`
  - adição de itens ao carrinho
  - soma de quantidade para produtos repetidos
  - remoção de itens
  - atualização de quantidade
  - cálculo de subtotal
  - aplicação de desconto percentual
  - cálculo do valor do desconto
  - cálculo do total final
  - verificação se o carrinho está vazio
  - proteção da lista interna com visualização não modificável

- `OrderItem`
  - snapshot dos dados do produto no momento da compra
  - ID do produto
  - nome do produto
  - preço do produto
  - quantidade
  - cálculo de subtotal por item

- `Order`
  - recebe ID por construtor
  - criado a partir do carrinho com `fromCart(...)`
  - usa `OrderItem` para desacoplar o pedido do `Product`
  - registra data/hora do pedido
  - armazena subtotal, desconto e total
  - geração de resumo textual
  - mudança de status via enum

- `OrderStatus`
  - estados `CREATED`, `COMPLETED` e `CANCELED`
  - controle de transições válidas de estado

---

#### Exceções personalizadas

- `InvalidDataException`
- `InvalidCepException`
- `InvalidCartException`
- `ProductNotFoundException`
- `ExternalServiceException`

---

#### Utilitários

- `EmailValidation`
  - validação de e-mail com regex

- `ZipValidation`
  - validação de CEP com regex
  - aceita apenas `XXXXXXXX` ou `XXXXX-XXX`
  - normalização segura removendo apenas o hífen

---

#### Infraestrutura

- `HttpConsumer`
  - responsável pela chamada HTTP com `HttpClient`
  - tratamento de falhas de rede
  - tratamento de interrupção
  - tratamento de status HTTP inválido

---

#### Serviços

- `ViaCepService`
  - validação prévia do CEP informado
  - normalização do CEP
  - consumo da API ViaCEP
  - conversão do JSON em `Address`
  - tratamento de CEP inexistente
  - tratamento de erro de processamento

- `CartService`
  - regra de desconto do carrinho
  - aplica 10% de desconto para subtotal acima de R$ 200,00
  - remove desconto quando a regra não é atendida

---

#### Repositórios

- `ProductRepository`
  - armazenamento em memória
  - geração do próximo ID com base no maior ID existente
  - listagem de produtos salvos

- `OrderRepository`
  - armazenamento em memória
  - geração do próximo ID com base no maior ID existente
  - listagem de pedidos salvos

---

#### Classe de entrada

- `Main`
  - usado atualmente para testes manuais do domínio
  - demonstra criação de cliente, produtos, carrinho e pedido
  - ainda não representa o fluxo final com menu interativo

---

## O que ainda está em andamento

- persistir produtos em JSON/arquivo
- persistir pedidos em JSON/arquivo
- carregar dados salvos ao iniciar o programa
- criar menu interativo com `Scanner`
- cadastrar e listar produtos pelo fluxo do console
- cadastrar cliente com busca de endereço por CEP
- finalizar compra de ponta a ponta pelo terminal
- melhorar a experiência de uso no `Main`

---

## Estrutura atual

```text
src/
└── main/
    ├── java/
    │   └── br/com/heraoliveira/hcommerce/
    │       ├── Main.java
    │       ├── exception/
    │       │   ├── ExternalServiceException.java
    │       │   ├── InvalidCartException.java
    │       │   ├── InvalidCepException.java
    │       │   ├── InvalidDataException.java
    │       │   └── ProductNotFoundException.java
    │       ├── infra/
    │       │   └── HttpConsumer.java
    │       ├── models/
    │       │   ├── Address.java
    │       │   ├── Cart.java
    │       │   ├── CartItem.java
    │       │   ├── Customer.java
    │       │   ├── Order.java
    │       │   ├── OrderItem.java
    │       │   ├── OrderStatus.java
    │       │   └── Product.java
    │       ├── repository/
    │       │   ├── OrderRepository.java
    │       │   └── ProductRepository.java
    │       ├── service/
    │       │   ├── CartService.java
    │       │   └── ViaCepService.java
    │       └── util/
    │           ├── EmailValidation.java
    │           └── ZipValidation.java
    ├── resources/
    └── test/
        └── java/