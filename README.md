# HCommerce

Projeto em desenvolvimento para praticar fundamentos de **Java** através de um mini sistema de compras no console.

A proposta deste repositório é consolidar conceitos importantes de orientação a objetos e modelagem de domínio antes de avançar para um segundo projeto mais robusto com **Spring Boot**.

## Objetivo

Construir um sistema simples de e-commerce em terminal, com foco em:

- modelagem de entidades
- encapsulamento e validações no construtor
- uso de `BigDecimal` para valores monetários
- composição entre classes do domínio
- tratamento de exceções personalizadas
- preparação para integração com API externa (ViaCEP)

## Estado atual do projeto

Até o momento, o projeto possui a modelagem inicial do domínio e algumas validações principais já implementadas.

### Já implementado

- `Product`
    - identificação automática
    - nome, descrição e preço
    - validações no construtor
    - `toString`, `equals` e `hashCode`

- `Customer`
    - nome, e-mail e endereço
    - validação de nome
    - validação de e-mail com classe utilitária separada

- `Address`
    - implementado como `record`
    - preparado para desserialização com Jackson
    - validações básicas de integridade dos campos

- `CartItem`
    - associação entre produto e quantidade
    - validação de item inválido
    - cálculo de subtotal do item

- Exceções personalizadas
    - `InvalidDataException`
    - `InvalidCepException`

- Classe utilitária
    - `EmailValidation` para validação de e-mail com regex

- `Main`
    - usado atualmente para testes manuais iniciais das classes

### Em andamento / próximos passos

- implementação da classe `Cart`
- menu interativo no console
- cadastro e listagem de produtos
- fluxo de carrinho completo
- integração com a API ViaCEP
- persistência de dados em arquivo/JSON
- organização de camadas de serviço e repositório

## Estrutura atual

```bash
src/
├── main/
│   ├── java/
│   │   └── br/com/heraoliveira/hcommerce/
│   │       ├── exception/
│   │       ├── models/
│   │       ├── repository/
│   │       ├── service/
│   │       ├── util/
│   │       └── Main.java
│   └── resources/
└── test/
    └── java/