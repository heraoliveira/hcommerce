# HCommerce

Projeto em desenvolvimento para praticar fundamentos de Java por meio de um mini sistema de compras no console.

A proposta deste repositório é consolidar conceitos importantes de orientação a objetos, modelagem de domínio, coleções, validações, exceções personalizadas e integração com API externa antes de evoluir para uma versão futura mais robusta.

## Objetivo

Construir um sistema simples de e-commerce em terminal, com foco em:

- modelagem de entidades e responsabilidades
- encapsulamento e validações no construtor
- uso de `BigDecimal` para valores monetários
- composição entre classes do domínio
- tratamento de exceções personalizadas
- integração com a API ViaCEP
- preparação para persistência em JSON/arquivo

## Tecnologias e dependências

- Java 17
- Maven
- Jackson Databind
- API ViaCEP
- `java.net.http.HttpClient`

## Estado atual do projeto

Neste momento, o projeto já possui uma base sólida de domínio e cobre bem a modelagem principal do mini e-commerce.

### Já implementado

#### Modelos de domínio

- `Product`
  - identificação automática
  - nome, descrição e preço
  - validações no construtor
  - alteração de preço com validação
  - `toString`, `equals` e `hashCode`

- `Customer`
  - nome, e-mail e endereço
  - validação de nome
  - validação de e-mail com classe utilitária separada
  - exigência de endereço válido no construtor

- `Address`
  - implementado como `record`
  - preparado para desserialização com Jackson
  - mapeamento dos campos retornados pela ViaCEP
  - validações básicas de integridade dos campos

- `CartItem`
  - associação entre produto e quantidade
  - validação de item inválido
  - cálculo de subtotal
  - incremento e atualização de quantidade
  - igualdade baseada no produto

- `Cart`
  - adição de itens ao carrinho
  - soma de quantidade para produtos repetidos
  - remoção de itens por id
  - atualização de quantidade por id
  - cálculo de subtotal, desconto e total
  - aplicação e limpeza de desconto percentual
  - verificação se o carrinho está vazio
  - proteção da lista interna com visualização não modificável
  - uso de `ProductNotFoundException` quando o item não existe no carrinho

- `Order`
  - identificação automática
  - criação a partir do carrinho com validações
  - snapshot dos itens da compra usando `OrderItem`
  - armazenamento de subtotal, desconto e total no momento da criação
  - registro de data/hora do pedido
  - geração de resumo textual
  - mudança de status por regras do enum

- `OrderItem`
  - snapshot dos dados essenciais do produto no momento da compra
  - armazenamento de id, nome, preço e quantidade
  - cálculo de subtotal próprio

- `OrderStatus`
  - estados `CREATED`, `COMPLETED` e `CANCELED`
  - controle de transições válidas de estado

#### Exceções personalizadas

- `InvalidDataException`
- `InvalidCepException`
- `InvalidCartException`
- `ProductNotFoundException`
- `ExternalServiceException`

#### Utilitários

- `EmailValidation`
  - validação de e-mail com regex

- `ZipValidation`
  - validação de CEP com regex
  - suporte a CEP com ou sem hífen

#### Infraestrutura e serviços

- `HttpConsumer`
  - consumo HTTP com `HttpClient`
  - tratamento de falhas de rede e interrupção

- `ViaCepService`
  - validação prévia do CEP informado
  - consumo da API ViaCEP
  - desserialização do JSON para `Address`
  - tratamento de CEP inexistente
  - tratamento de falhas de integração por exceção customizada

- `CartService`
  - regra de desconto simples
  - aplicação automática de 10% para carrinhos acima de R$ 200,00

#### Classe de entrada

- `Main`
  - usado atualmente para testes manuais das classes do domínio
  - ainda não representa o fluxo final do sistema

## O que ainda está em andamento

- criar menu interativo com `Scanner`
- cadastrar e listar produtos pelo fluxo do console
- cadastrar cliente com busca de endereço por CEP no fluxo principal
- finalizar compra de ponta a ponta pelo terminal
- persistir produtos e pedidos em arquivo/JSON
- implementar a camada `repository`
- organizar melhor o fluxo da aplicação no `Main`
- adicionar testes automatizados
- revisar mensagens, nomenclatura e formatação para apresentação final do portfólio

## Estrutura atual

```text
src/
├── main/
│   ├── java/
│   │   └── br/com/heraoliveira/hcommerce/
│   │       ├── Main.java
│   │       ├── exception/
│   │       │   ├── ExternalServiceException.java
│   │       │   ├── InvalidCartException.java
│   │       │   ├── InvalidCepException.java
│   │       │   ├── InvalidDataException.java
│   │       │   └── ProductNotFoundException.java
│   │       ├── infra/
│   │       │   └── HttpConsumer.java
│   │       ├── models/
│   │       │   ├── Address.java
│   │       │   ├── Cart.java
│   │       │   ├── CartItem.java
│   │       │   ├── Customer.java
│   │       │   ├── Order.java
│   │       │   ├── OrderItem.java
│   │       │   ├── OrderStatus.java
│   │       │   └── Product.java
│   │       ├── service/
│   │       │   ├── CartService.java
│   │       │   └── ViaCepService.java
│   │       └── util/
│   │           ├── EmailValidation.java
│   │           └── ZipValidation.java
│   └── resources/
└── test/
    └── java/
```