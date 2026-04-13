# Sicredi - API de Aquisicao de Credito

API REST para contratacao e consulta de operacoes de credito, desenvolvida como parte do Desafio Tecnico Sicredi - Area Agro.

**Empresa:** DB - Unidade AR
**Responsavel:** Onisio Simoes Queiroz Junior
**Telefone:** (79) 99648-7727

---

## Tecnologias Utilizadas

| Tecnologia | Versao | Finalidade |
|---|---|---|
| Java | 21 (LTS) | Linguagem principal |
| Quarkus | 3.17.5 | Framework backend |
| Gradle (Kotlin DSL) | 8.11 | Build e gerenciamento de dependencias |
| Hibernate ORM com Panache | - | Persistencia e acesso a dados |
| PostgreSQL | - | Banco de dados relacional |
| RESTEasy Reactive | - | Endpoints REST |
| MicroProfile REST Client | - | Integracao com servico externo |
| SmallRye OpenAPI + Swagger UI | - | Documentacao interativa da API |
| JUnit 5 + Mockito | - | Testes unitarios |
| RestAssured | - | Testes de integracao dos endpoints |

---

## Arquitetura

Arquitetura em camadas classica:

```
Controller (endpoints REST)
    |
Service (regras de negocio)
    |
Repository (acesso a dados via Panache)
    |
PostgreSQL
```

### Estrutura do Projeto

```
src/main/java/br/com/sicredi/credito/
├── controller/
│   └── OperacaoCreditoController.java    # Endpoints POST e GET
├── service/
│   └── OperacaoCreditoService.java       # Regras de negocio
├── repository/
│   ├── TituloCreditoRepository.java      # Repositorio de titulos
│   └── VinculoSocioBeneficiarioRepository.java  # Repositorio de vinculos PJ
├── entity/
│   ├── TituloCredito.java                # Entidade principal
│   └── VinculoSocioBeneficiario.java     # Entidade de vinculo socio (PJ)
├── dto/
│   ├── ContratacaoRequest.java           # Dados de entrada da contratacao
│   ├── ContratacaoResponse.java          # Retorno com ID da operacao
│   ├── ConsultaResponse.java             # Retorno da consulta
│   └── PermiteContratarResponse.java     # Resposta do servico externo
├── client/
│   └── ProdutoCreditoClient.java         # Cliente REST para servico de produtos
├── enums/
│   └── Segmento.java                     # PF, PJ, AGRO
└── filter/
    └── AuthFilter.java                   # Filtro de autenticacao por token
```

---

## Modelo de Dados

### Tabela `titulo_credito`

| Coluna | Tipo | Descricao |
|---|---|---|
| `id` | `UUID` (PK) | Identificador unico da operacao de credito |
| `id_associado` | `BIGINT` | ID do associado |
| `valor_operacao` | `DECIMAL` | Valor a ser liberado na conta |
| `segmento` | `VARCHAR(4)` | PF, PJ ou AGRO |
| `codigo_produto_credito` | `VARCHAR(10)` | Codigo do produto de credito |
| `codigo_conta` | `VARCHAR(10)` | Conta corrente do associado |
| `area_beneficiada_ha` | `DECIMAL` (nullable) | Area beneficiada em hectares (AGRO) |
| `data_contratacao` | `TIMESTAMP` | Data e hora da contratacao |

### Tabela `vinculo_socio_beneficiario`

| Coluna | Tipo | Descricao |
|---|---|---|
| `id` | `BIGINT` (PK) | Identificador do registro |
| `id_operacao_credito` | `UUID` (FK) | Referencia ao titulo de credito |
| `id_associado` | `BIGINT` | ID do socio beneficiario |

> A tabela `vinculo_socio_beneficiario` e populada apenas para operacoes do segmento **PJ**.

---

## Endpoints da API

### POST `/api/operacoes-credito`

Cria uma nova operacao de credito.

**Request:**
```json
{
  "idAssociado": 12345,
  "valorOperacao": 5000,
  "segmento": "PF",
  "codigoProdutoCredito": "101A",
  "codigoConta": "0123456789",
  "areaBeneficiadaHa": null
}
```

**Response (201 Created):**
```json
{
  "idOperacaoCredito": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

| Codigo | Descricao |
|---|---|
| 201 | Operacao contratada com sucesso |
| 401 | Token nao informado |
| 403 | Token invalido |
| 422 | Contratacao recusada (regra de negocio) |
| 503 | Servico de produtos indisponivel |

### GET `/api/operacoes-credito/{id}`

Consulta uma operacao de credito pelo ID.

**Response (200 OK):**
```json
{
  "idOperacaoCredito": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "idAssociado": 12345,
  "valorOperacao": 5000,
  "segmento": "PF",
  "codigoProdutoCredito": "101A",
  "codigoConta": "0123456789",
  "areaBeneficiadaHa": null,
  "dataContratacao": "2026-04-13T10:30:00"
}
```

| Codigo | Descricao |
|---|---|
| 200 | Operacao encontrada |
| 401 | Token nao informado |
| 403 | Token invalido |
| 404 | Operacao nao encontrada |

---

## Regras de Negocio

### 1. Validacao do Produto de Credito

Antes de salvar a operacao, o sistema consulta um servico externo para verificar se o produto de credito permite a contratacao com o segmento e valor informados. O servico retorna `{ "permiteContratar": true/false }`.

**Produtos disponiveis:**

| Produto | Segmentos | Valor Minimo | Valor Maximo |
|---|---|---|---|
| 903C | AGRO | 1.000 | 10.000 |
| 101A | PF | 500 | 5.000 |
| 202B | PJ | 5.000 | 50.000 |
| 303C | AGRO | 2.000 | 15.000 |
| 404D | PF | 1.000 | 20.000 |
| 505E | PJ | 10.000 | 100.000 |
| 606F | PF, PJ | 2.000 | 30.000 |
| 707G | AGRO, PF | 1.500 | 12.000 |
| 808H | AGRO, PJ | 5.000 | 60.000 |
| 909I | PF, PJ, AGRO | 1.000 | 50.000 |

### 2. Validacao AGRO

Operacoes do segmento **AGRO** so podem ser contratadas quando o campo `areaBeneficiadaHa` estiver preenchido e for **maior que zero**. Caso contrario, a contratacao e recusada com status 422.

### 3. Vinculo PJ

Operacoes do segmento **PJ** geram automaticamente um registro na tabela `vinculo_socio_beneficiario`, vinculando a operacao ao associado que solicitou o credito.

### 4. Tratamento de Indisponibilidade

Caso o servico externo de produtos esteja fora do ar (timeout, erro de conexao), a API retorna status **503 Service Unavailable**.

---

## Autenticacao

A API utiliza autenticacao por **Bearer Token**. Todas as requisicoes aos endpoints devem incluir o header:

```
Authorization: Bearer sicredi-credito-2026
```

O token pode ser configurado no `application.properties` pela propriedade `api.token`.

---

## Como Executar

### Pre-requisitos

- Java 21
- PostgreSQL (com banco `credito_sicredi` criado)

### Configuracao

Edite `src/main/resources/application.properties` com os dados do seu banco:

```properties
quarkus.datasource.username=seu_usuario
quarkus.datasource.password=sua_senha
quarkus.datasource.jdbc.url=jdbc:postgresql://seu_host:5432/credito_sicredi
```

### Executar em modo desenvolvimento

```bash
./gradlew quarkusDev
```

### Mock do servico de produtos (em outro terminal)

```bash
python mock-server.py
```

### Acessar documentacao interativa

```
http://localhost:8080/q/swagger-ui
```

### Executar testes

```bash
./gradlew test
```

---

## Testes

### Testes Unitarios (Service)

| Teste | Cenario |
|---|---|
| `deveCriarOperacaoComSucesso` | Fluxo feliz - cria titulo de credito PF |
| `deveCriarOperacaoPJComVinculo` | Segmento PJ - cria titulo + vinculo socio |
| `deveRejeitarAgroSemArea` | AGRO sem areaBeneficiadaHa - retorna 422 |
| `deveRejeitarAgroComAreaZero` | AGRO com area = 0 - retorna 422 |
| `deveRejeitarQuandoProdutoNaoPermite` | Produto nao permite contratacao - retorna 422 |
| `deveRetornar503QuandoServicoIndisponivel` | Servico externo fora do ar - retorna 503 |

### Testes de Integracao (Controller)

| Teste | Cenario |
|---|---|
| `deveContratarERetornarId` | POST retorna 201 com UUID |
| `deveConsultarOperacaoCriada` | GET retorna 200 com dados completos |
| `deveRetornar404ParaOperacaoInexistente` | GET com UUID inexistente - retorna 404 |

---

## Fluxo da Contratacao

```
1. Recebe requisicao POST com dados da contratacao
2. Se segmento == AGRO:
   └── Valida areaBeneficiadaHa (preenchido e > 0)
       └── Se invalido: retorna 422
3. Consulta servico externo de produtos:
   └── Se indisponivel: retorna 503
   └── Se permiteContratar == false: retorna 422
4. Persiste TituloCredito no banco (UUID gerado + timestamp)
5. Se segmento == PJ:
   └── Persiste VinculoSocioBeneficiario
6. Retorna idOperacaoCredito (UUID)
```
