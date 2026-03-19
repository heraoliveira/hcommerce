# HCommerce

Projeto em desenvolvimento para praticar fundamentos de **Java** por meio de um mini sistema de compras no console.

A proposta deste repositório é consolidar conceitos importantes de orientação a objetos, modelagem de domínio, coleções, validações e tratamento de exceções antes da evolução para uma versão futura mais robusta.

## Objetivo

Construir um sistema simples de e-commerce em terminal, com foco em:

- modelagem de entidades e responsabilidades
- encapsulamento e validações no construtor
- uso de `BigDecimal` para valores monetários
- composição entre classes do domínio
- tratamento de exceções personalizadas
- integração inicial com API externa (ViaCEP)
- preparação para persistência em JSON/arquivo

## Estado atual do projeto

Neste momento, o projeto já possui a base principal do domínio, regras centrais do carrinho, geração de pedido, utilitários de validação e o início da integração com ViaCEP.

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
  - remoção de itens
  - atualização de quantidade
  - cálculo do valor total
  - aplicação de desconto percentual
  - verificação se o carrinho está vazio
  - proteção da lista interna com visualização não modificável

- `Order`
  - identificação automática
  - cópia defensiva dos itens recebidos do carrinho
  - cálculo do total no momento da criação
  - registro de data/hora do pedido
  - geração de resumo textual do pedido
  - mudança de status por regras do enum

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

#### Serviço

- `ViaCepService`
  - validação prévia do CEP informado
  - consumo inicial da API ViaCEP com `HttpClient`
  - tratamento de falhas de integração por exceção customizada

#### Classe de entrada

- `Main`
  - usado atualmente para testes manuais das classes do domínio
  - ainda não representa o fluxo final do sistema

## O que ainda está em andamento

- concluir a integração da ViaCEP retornando `Address` em vez de JSON bruto
- tratar explicitamente CEP inexistente na resposta da API
- ajustar o fluxo de criação de `Customer` no `Main`
- criar menu interativo com `Scanner`
- cadastrar e listar produtos pelo fluxo do console
- cadastrar cliente com busca de endereço por CEP
- finalizar compra de ponta a ponta pelo terminal
- persistir produtos e pedidos em arquivo/JSON
- implementar classes reais da camada `repository`

## Observações importantes sobre o estado atual

O projeto já possui uma base sólida de domínio, mas ainda está em fase de construção.

Hoje, a integração com ViaCEP foi iniciada, porém ainda está parcial:

- o serviço faz a chamada HTTP
- o CEP é validado antes da requisição
- mas a resposta ainda é retornada como `String`
- e o tratamento de CEP inexistente ainda não foi concluído

Além disso, o `Main` continua sendo apenas um ambiente de teste manual. O fluxo completo do sistema ainda será reorganizado nas próximas etapas.

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
    │       ├── models/
    │       │   ├── Address.java
    │       │   ├── Cart.java
    │       │   ├── CartItem.java
    │       │   ├── Customer.java
    │       │   ├── Order.java
    │       │   ├── OrderStatus.java
    │       │   └── Product.java
    │       ├── repository/
    │       ├── service/
    │       │   └── ViaCepService.java
    │       └── util/
    │           ├── EmailValidation.java
    │           └── ZipValidation.java
    ├── resources/
    └── test/
        └── java/