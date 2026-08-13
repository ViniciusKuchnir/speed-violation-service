# Speed Violation Service

Microserviço REST responsável por processar leituras de velocidade captadas por
equipamentos de fiscalização de trânsito, aplicar regras de tolerância e
identificar possíveis infrações por excesso de velocidade.

O projeto foi desenvolvido em Java 21 com Spring Boot 3 e inclui validação de
entrada, regras de negócio, persistência concorrente em memória, testes
automatizados, documentação OpenAPI, containerização, integração contínua e
deploy público.

---

## Quick Access

| Recurso | Link |
| ------- | ---- |
| API pública | [https://speed-violation-service-cgk1.onrender.com](https://speed-violation-service-cgk1.onrender.com) |
| Swagger UI | [https://speed-violation-service-cgk1.onrender.com/swagger-ui.html](https://speed-violation-service-cgk1.onrender.com/swagger-ui.html) |
| OpenAPI JSON | [https://speed-violation-service-cgk1.onrender.com/v3/api-docs](https://speed-violation-service-cgk1.onrender.com/v3/api-docs) |
| Health Check | [https://speed-violation-service-cgk1.onrender.com/actuator/health](https://speed-violation-service-cgk1.onrender.com/actuator/health) |

> **Observação:** a aplicação está hospedada em uma instância gratuita do Render.
> Após aproximadamente 15 minutos sem tráfego, o serviço pode entrar em estado de
> inatividade. Nesse caso, a primeira requisição pode levar cerca de um minuto
> enquanto a instância é iniciada novamente.

---

## Table of Contents

- [Overview](#overview)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API](#api)
- [API Documentation](#api-documentation)
- [Architecture](#architecture)
- [Tests and Coverage](#tests-and-coverage)
- [Continuous Integration](#continuous-integration)
- [Deployment](#deployment)
- [API Collection](#api-collection)
- [Requirements](#requirements)
- [Future Improvements](#future-improvements)
- [Commit Convention](#commit-convention)

---

## Overview

O serviço recebe leituras de velocidade, valida os dados recebidos, aplica a
margem de tolerância correspondente à velocidade regulamentada e determina se
existe infração.

Quando existe infração, o serviço calcula o percentual de excesso e realiza sua
classificação conforme a regra definida pelo domínio.

### Principais características

- validação de placas nos formatos antigo e Mercosul;
- validação de velocidades, equipamento, timestamp e origem;
- aplicação automática da margem de tolerância;
- cálculo do percentual de excesso;
- classificação da infração;
- persistência exclusivamente de infrações;
- armazenamento concorrente em memória;
- consulta de infrações por placa;
- respostas de erro padronizadas;
- documentação OpenAPI e Swagger UI;
- testes unitários e de integração;
- cobertura automatizada com JaCoCo;
- containerização com Docker;
- integração contínua com GitHub Actions;
- deploy público no Render.

### Status do projeto

| Categoria | Status |
| --------- | :----: |
| Requisitos Funcionais | ✅ 6/6 |
| Requisitos Não Funcionais | ✅ 5/5 |
| Diferenciais propostos | ✅ 6/6 |

---

## Technologies

| Categoria | Tecnologias |
| --------- | ----------- |
| Linguagem | Java 21 |
| Framework | Spring Boot 3 |
| Build | Maven |
| Testes | JUnit 5, AssertJ, MockMvc |
| Cobertura | JaCoCo |
| Documentação | Springdoc OpenAPI, Swagger UI |
| Containerização | Docker |
| CI | GitHub Actions |
| Hospedagem | Render |

---

## Getting Started

### Pré-requisitos

Para executar localmente:

- Java 21;
- Git.

Para execução via container:

- Docker.

### Executar localmente

Após clonar o repositório, acesse o diretório do projeto.

#### Windows

```bash
.\mvnw.cmd spring-boot:run
```

#### Linux/macOS

```bash
./mvnw spring-boot:run
```

Por padrão, a aplicação estará disponível em:

```text
http://localhost:8080
```

### Executar com Docker

A aplicação possui um `Dockerfile` multi-stage utilizando Java 21.

O estágio de build utiliza o Maven Wrapper do projeto para gerar o artefato,
enquanto a imagem final contém apenas o runtime necessário para executar a
aplicação.

#### Build da imagem

```bash
docker build -t speed-violation-service .
```

#### Executar o container

```bash
docker run --name speed-violation-service --rm -p 8080:8080 speed-violation-service
```

A aplicação estará disponível em:

```text
http://localhost:8080
```

#### Executar em outra porta

A porta pode ser alterada através da variável de ambiente `SERVER_PORT`.

Exemplo utilizando a porta `9090`:

```bash
docker run --name speed-violation-service --rm -e SERVER_PORT=9090 -p 9090:9090 speed-violation-service
```

A aplicação estará disponível em:

```text
http://localhost:9090
```

---

## Configuration

As configurações operacionais são definidas em `application.properties` e podem
ser sobrescritas através de variáveis de ambiente.

| Propriedade | Valor padrão |
| ----------- | -----------: |
| `server.port` | `8080` |
| `violation.tolerance.fixed-kmh` | `7` |
| `violation.tolerance.percentage` | `7` |
| `violation.tolerance.percentage-threshold` | `100` |

A externalização permite alterar parâmetros operacionais entre ambientes sem
modificar o código-fonte.

---

## API

### Base URLs

| Ambiente | Base URL |
| -------- | -------- |
| Local | `http://localhost:8080` |
| Hospedado | `https://speed-violation-service-cgk1.onrender.com` |

### Endpoints

| Método | Endpoint | Descrição |
| :----: | -------- | --------- |
| `POST` | `/api/v1/violations/evaluate` | Avalia uma leitura de velocidade |
| `GET` | `/api/v1/violations?licensePlate={licensePlate}` | Consulta infrações por placa |
| `GET` | `/actuator/health` | Verifica a saúde da aplicação |

---

### Avaliar velocidade

```http
POST /api/v1/violations/evaluate
```

Header obrigatório:

```text
x-origin: FIXED
```

Valores aceitos:

```text
FIXED
MOBILE
HANDHELD
```

Formatos de placa aceitos:

| Formato | Exemplo |
| ------- | ------- |
| Antigo | `ABC1234` |
| Mercosul | `ABC1D23` |

#### Exemplo com infração

```bash
curl -X POST http://localhost:8080/api/v1/violations/evaluate \
  -H "Content-Type: application/json" \
  -H "x-origin: FIXED" \
  -d '{
    "licensePlate": "ABC1D23",
    "measuredSpeed": 92,
    "speedLimit": 60,
    "equipmentId": "RAD-CWB-001",
    "captureTimestamp": "2026-06-08T14:30:00Z"
  }'
```

Resposta:

```json
{
  "licensePlate": "ABC1D23",
  "equipmentId": "RAD-CWB-001",
  "measuredSpeed": 92,
  "consideredSpeed": 85,
  "speedLimit": 60,
  "excessPercentage": 41.67,
  "hasViolation": true,
  "violation": {
    "severity": "SERIOUS",
    "ctbCode": "218-II"
  },
  "processedAt": "2026-06-08T14:30:05Z"
}
```

#### Exemplo sem infração

```bash
curl -X POST http://localhost:8080/api/v1/violations/evaluate \
  -H "Content-Type: application/json" \
  -H "x-origin: FIXED" \
  -d '{
    "licensePlate": "ABC1D23",
    "measuredSpeed": 64,
    "speedLimit": 60,
    "equipmentId": "RAD-CWB-001",
    "captureTimestamp": "2026-06-08T14:30:00Z"
  }'
```

Resposta:

```json
{
  "licensePlate": "ABC1D23",
  "equipmentId": "RAD-CWB-001",
  "measuredSpeed": 64,
  "consideredSpeed": 57,
  "speedLimit": 60,
  "excessPercentage": 0.00,
  "hasViolation": false,
  "violation": null,
  "processedAt": "2026-06-08T14:30:05Z"
}
```

---

### Consultar infrações por placa

```http
GET /api/v1/violations?licensePlate=ABC1D23
```

Exemplo:

```bash
curl "http://localhost:8080/api/v1/violations?licensePlate=ABC1D23"
```

Resposta:

```json
[
  {
    "licensePlate": "ABC1D23",
    "equipmentId": "RAD-CWB-001",
    "measuredSpeed": 92,
    "consideredSpeed": 85,
    "speedLimit": 60,
    "excessPercentage": 41.67,
    "hasViolation": true,
    "violation": {
      "severity": "SERIOUS",
      "ctbCode": "218-II"
    },
    "processedAt": "2026-06-08T14:30:05Z"
  }
]
```

Quando não existem infrações registradas para a placa:

```json
[]
```

Somente avaliações que resultam em infração são armazenadas.

A persistência é realizada exclusivamente em memória. Portanto, os registros são
removidos quando a aplicação é reiniciada.

---

### Erros de validação

Entradas inválidas retornam `400 Bad Request` utilizando um formato padronizado.

Exemplo:

```bash
curl -X POST http://localhost:8080/api/v1/violations/evaluate \
  -H "Content-Type: application/json" \
  -H "x-origin: FIXED" \
  -d '{
    "licensePlate": "ABC1AA9",
    "measuredSpeed": 92,
    "speedLimit": 60,
    "equipmentId": "RAD-CWB-001",
    "captureTimestamp": "2026-06-08T14:30:00Z"
  }'
```

Resposta:

```json
{
  "error": "INVALID_LICENSE_PLATE",
  "message": "Invalid license plate format",
  "timestamp": "2026-08-12T22:00:00Z"
}
```

Erros inesperados retornam `500 Internal Server Error` com mensagem genérica,
sem exposição de stack traces ou detalhes internos da aplicação.

---

### Health Check

```http
GET /actuator/health
```

Exemplo:

```bash
curl http://localhost:8080/actuator/health
```

Resposta:

```json
{
  "status": "UP",
  "groups": [
    "liveness",
    "readiness"
  ]
}
```

O campo `status` igual a `UP` indica que a aplicação está disponível.

---

## API Documentation

A API possui documentação OpenAPI gerada através do Springdoc e uma interface
interativa disponibilizada através do Swagger UI.

### Ambiente local

| Recurso | URL |
| ------- | --- |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |

### Ambiente hospedado

| Recurso | URL |
| ------- | --- |
| Swagger UI | `https://speed-violation-service-cgk1.onrender.com/swagger-ui.html` |
| OpenAPI JSON | `https://speed-violation-service-cgk1.onrender.com/v3/api-docs` |

A documentação apresenta:

- endpoints disponíveis;
- header obrigatório `x-origin`;
- parâmetros de entrada;
- schemas de request e response;
- valores aceitos para enums;
- respostas `200`, `400` e `500`;
- formato padronizado das respostas de erro.

O Swagger UI também permite executar requisições diretamente pela interface.

---

## Architecture

O projeto utiliza **package by feature**, concentrando a funcionalidade relacionada
à apuração de infrações em `features/violation`.

Dentro da feature, as responsabilidades são separadas em camadas convencionais
do ecossistema Spring.

```text
features/
└── violation/
    ├── controller/
    ├── dto/
    ├── exception/
    ├── mapper/
    ├── model/
    ├── provider/
    ├── repository/
    ├── service/
    └── validator/
```

### Fluxo principal

```text
HTTP Request
     │
     ▼
Controller
     │
     ▼
Service
     │
     ▼
Repository
     │
     ▼
In-Memory Provider
```

O controller recebe e valida o contrato HTTP, enquanto o service concentra as
regras de negócio. O repository define a abstração de persistência e o provider
fornece sua implementação atual em memória.

### Decisões técnicas e arquiteturais

- **Package by feature:** mantém os componentes relacionados ao mesmo domínio
  agrupados, facilitando manutenção e evolução.
- **Separação em camadas:** controller, service, repository e provider possuem
  responsabilidades distintas.
- **DTOs:** representam os contratos da API sem expor diretamente os modelos
  internos.
- **Mapper:** centraliza a conversão entre modelos internos e DTOs de resposta.
- **Jakarta Bean Validation:** utilizada para validações simples dos dados de
  entrada.
- **Validação customizada de placa:** `@ValidLicensePlate` concentra as regras dos
  formatos antigo e Mercosul.
- **Regex pré-compiladas:** os padrões de placa são mantidos como constantes
  estáticas no validator.
- **Origem tipada:** `x-origin` é representado por enum com os valores `FIXED`,
  `MOBILE` e `HANDHELD`.
- **Tratamento centralizado de erros:** `@RestControllerAdvice` padroniza falhas de
  validação e erros inesperados.
- **Respostas seguras:** stack traces e detalhes internos permanecem nos logs e não
  são expostos ao cliente.
- **Regras no service:** cálculos de tolerância, percentual de excesso e
  classificação permanecem concentrados na camada de negócio.
- **Repository Pattern:** a persistência é acessada através de uma abstração.
- **Provider em memória:** permite substituir a implementação de armazenamento sem
  alterar as regras de negócio.
- **Persistência condicionada:** somente avaliações que resultam em infração são
  armazenadas.
- **Concorrência:** o armazenamento utiliza estruturas concorrentes para suportar
  múltiplos acessos.
- **Modelos imutáveis:** Records são utilizados quando apropriado.
- **Precisão decimal:** `BigDecimal` evita imprecisões de ponto flutuante no
  cálculo percentual.
- **Classificação tipada:** `ViolationSeverity` representa a severidade da
  infração.
- **Configuração externalizada:** parâmetros operacionais podem ser alterados sem
  modificar o código.
- **TDD:** testes foram utilizados como apoio à evolução das regras e
  funcionalidades.
- **Cobertura automatizada:** JaCoCo valida a cobertura durante a fase `verify`.
- **OpenAPI:** contratos e respostas da API são documentados através do Springdoc.
- **Docker:** multi-stage build separa compilação e runtime.
- **Integração contínua:** GitHub Actions valida build, testes, cobertura e imagem
  Docker.
- **Deploy:** o mesmo `Dockerfile` é utilizado para disponibilizar a aplicação no
  Render.

---

## Tests and Coverage

O projeto possui testes unitários para:

- regras de negócio;
- cálculos de tolerância;
- limites de classificação;
- persistência em memória;
- validação dos formatos de placa.

Também possui testes de integração utilizando Spring Boot e MockMvc para validar:

- avaliação com e sem infração;
- validação de placa;
- validação das velocidades;
- validação do equipamento;
- validação do timestamp;
- validação do header `x-origin`;
- respostas de erro padronizadas;
- tratamento de erros inesperados `500`;
- ausência de exposição de detalhes internos;
- consulta de infrações por placa;
- retorno de lista vazia;
- garantia de que avaliações sem infração não sejam persistidas.

### Coverage

A cobertura é analisada através do JaCoCo e validada automaticamente durante a
fase `verify` do Maven.

| Escopo | Cobertura de linhas | Cobertura de branches |
| ------ | -------------------: | ---------------------: |
| `ViolationService` | 100% (46/46) | 100% (14/14) |
| Projeto completo | 97,9% (184/188) | 90,9% (40/44) |

O build falha caso a cobertura de linhas da camada de negócio fique abaixo de
80%.

### Executar os testes

#### Windows

```bash
.\mvnw.cmd test
```

#### Linux/macOS

```bash
./mvnw test
```

### Validar testes e cobertura

#### Windows

```bash
.\mvnw.cmd clean verify
```

#### Linux/macOS

```bash
./mvnw clean verify
```

O relatório HTML do JaCoCo é gerado em:

```text
target/site/jacoco/index.html
```

---

## Continuous Integration

O projeto utiliza GitHub Actions para executar automaticamente o processo de
integração contínua em `pushes` e `pull requests`.

O workflow está localizado em:

```text
.github/workflows/ci.yml
```

O pipeline executa:

```text
Checkout
   │
   ▼
Java 21
   │
   ▼
Maven clean verify
   │
   ├── Build
   ├── Testes
   └── JaCoCo
   │
   ▼
Docker build
```

As principais etapas são:

1. checkout do repositório;
2. configuração do Java 21;
3. configuração da permissão do Maven Wrapper;
4. execução de `./mvnw --batch-mode clean verify`;
5. build da imagem Docker.

O pipeline somente é concluído com sucesso quando compilação, testes, cobertura
e construção da imagem Docker são concluídos sem erros.

---

## Deployment

A aplicação está hospedada publicamente no **Render** utilizando o `Dockerfile`
do projeto.

| Recurso | URL |
| ------- | --- |
| API | `https://speed-violation-service-cgk1.onrender.com` |
| Swagger UI | `https://speed-violation-service-cgk1.onrender.com/swagger-ui.html` |
| OpenAPI JSON | `https://speed-violation-service-cgk1.onrender.com/v3/api-docs` |
| Health Check | `https://speed-violation-service-cgk1.onrender.com/actuator/health` |

O ambiente hospedado utiliza configurações fornecidas através de variáveis de
ambiente e executa a mesma aplicação containerizada validada localmente.

### Free instance behavior

A aplicação utiliza uma instância gratuita do Render.

Após aproximadamente 15 minutos sem receber tráfego, o serviço pode entrar em
estado de inatividade. A primeira requisição seguinte inicia novamente a
instância e pode levar cerca de um minuto para ser respondida.

Caso o primeiro acesso à API ou ao Swagger apresente um tempo de resposta maior,
aguarde a inicialização da instância e tente novamente.

### Persistência

A persistência definida para o projeto é exclusivamente em memória.

Consequentemente, as infrações registradas existem somente durante o ciclo de
vida da instância atual. Reinicializações, novos deploys ou recriações da
instância resultam em um armazenamento vazio.

Esse comportamento é intencional e está de acordo com o requisito de persistência
em memória definido para o projeto.

---

## API Collection

Uma collection do Insomnia está disponível em:

```text
insomnia/speed-violation-service.yaml
```

Ela contém exemplos para:

- avaliação com infração;
- avaliação sem infração;
- consulta de infrações por placa;
- consulta sem infrações;
- consulta com placa inválida;
- health check da aplicação.

URL base local:

```text
http://localhost:8080
```

Para testar o ambiente hospedado:

```text
https://speed-violation-service-cgk1.onrender.com
```

---

## Requirements

Todos os requisitos definidos para a implementação foram concluídos.

### Functional Requirements

| ID | Status | Descrição |
| -- | :----: | --------- |
| RF1 | ✅ | Apuração de velocidade através de `POST /api/v1/violations/evaluate` |
| RF2 | ✅ | Validação de placa, velocidades, equipamento, timestamp e `x-origin` |
| RF3 | ✅ | Aplicação das regras de tolerância, excesso e classificação |
| RF4 | ✅ | Persistência concorrente em memória e consulta por placa |
| RF5 | ✅ | Tratamento centralizado e padronizado de erros |
| RF6 | ✅ | Tratamento de casos de fronteira e situações especiais |

### Non-Functional Requirements

| ID | Status | Descrição |
| -- | :----: | --------- |
| RNF1 | ✅ | Organização e separação de responsabilidades |
| RNF2 | ✅ | Configuração externalizada |
| RNF3 | ✅ | Testes e cobertura mínima automatizada |
| RNF4 | ✅ | Documentação de execução e da API |
| RNF5 | ✅ | Java 21, código limpo, Records e boas práticas |

### Differentials

| Diferencial | Status |
| ----------- | :----: |
| Swagger / OpenAPI | ✅ |
| Dockerfile | ✅ |
| Testes de integração | ✅ |
| Collection Postman / Insomnia | ✅ |
| Pipeline CI | ✅ |
| Aplicação hospedada | ✅ |

---

## Future Improvements

A implementação atual foi mantida intencionalmente compatível com o escopo do
desafio.

Em um cenário real de produção, decisões relacionadas à escalabilidade e à
arquitetura deveriam ser tomadas a partir do comportamento observado da
aplicação e de métricas concretas, evitando adicionar complexidade sem uma
necessidade comprovada.

### Load Testing

Antes de introduzir mudanças arquiteturais relacionadas à escalabilidade, seriam
realizados testes de carga para compreender o comportamento da aplicação sob
diferentes volumes de requisições.

Os testes permitiriam observar fatores como:

- throughput das avaliações;
- tempo de resposta;
- comportamento durante picos;
- possíveis gargalos;
- relação entre volume de avaliações e consultas.

Existe uma hipótese derivada do domínio de que o volume de avaliações pode ser
significativamente superior ao volume de consultas por placa, já que cada
passagem de um veículo por um equipamento pode originar uma avaliação.

Essa hipótese não é tratada como uma característica garantida. Os resultados dos
testes de carga e, posteriormente, métricas reais de produção seriam utilizados
como evidência para orientar decisões arquiteturais.

### CQRS

Caso os testes de carga e as métricas de produção confirmassem necessidades
operacionais significativamente diferentes entre escrita e leitura, uma possível
evolução seria a adoção de **CQRS (Command Query Responsibility Segregation)**.

Nesse cenário:

```text
POST /api/v1/violations/evaluate
             │
             ▼
          Command
```

enquanto:

```text
GET /api/v1/violations
             │
             ▼
           Query
```

Os dois caminhos poderiam ser evoluídos, otimizados e escalados
independentemente caso apresentassem características diferentes de carga.

CQRS não foi aplicado na implementação atual porque não existem métricas que
demonstrem essa necessidade.

A decisão seria baseada na sequência:

```text
Hipótese de domínio
        │
        ▼
Testes de carga
        │
        ▼
Métricas de produção
        │
        ▼
Decisão arquitetural
```

Dessa forma, a arquitetura evoluiria a partir de evidências em vez de
complexidade antecipada.

### Idempotency

Em um ambiente real, equipamentos ou serviços responsáveis pelo envio das
leituras podem repetir uma requisição devido a timeout, instabilidade de rede ou
mecanismos de retry.

Sem idempotência, uma mesma captura poderia ser processada mais de uma vez e
resultar em registros duplicados.

Uma evolução seria associar cada captura a um identificador único, permitindo
que o serviço reconhecesse requisições previamente processadas.

Conceitualmente:

```text
Capture ID: 987654

1ª requisição
      │
      ▼
Processada

2ª requisição com mesmo ID
      │
      ▼
Duplicidade identificada
```

Essa característica se tornaria ainda mais relevante caso a aplicação evoluísse
para processamento distribuído ou assíncrono.

---

## Commit Convention

O histórico do projeto foi organizado utilizando commits pequenos e focados em
uma única responsabilidade.

As mensagens seguem um padrão inspirado em **Conventional Commits**:

```text
<type>: <description>
```

### Tipos utilizados

| Tipo | Utilização |
| ---- | ---------- |
| `feat` | Implementação de novas funcionalidades |
| `test` | Criação ou alteração de testes |
| `refactor` | Mudanças estruturais sem alteração de comportamento |
| `build` | Build, dependências ou Docker |
| `ci` | Integração contínua |
| `docs` | Documentação, OpenAPI ou collections |

As descrições são mantidas curtas, em inglês e representam diretamente a
responsabilidade da alteração.

Exemplos:

```text
feat: add violation query endpoint

test: add violation query integration tests

refactor: reuse violation response mapper

build: add Docker image configuration

ci: add build and test workflow

docs: document hosted application
```

A granularidade dos commits facilita a revisão do histórico, a identificação das
decisões tomadas durante o desenvolvimento e a análise isolada de cada mudança.