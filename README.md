# Speed Violation Service

Microserviço REST responsável por processar leituras de velocidade captadas por equipamentos de fiscalização de trânsito, aplicar as regras de tolerância e identificar possíveis infrações por excesso de velocidade.

---

## Technologies

- Java 21
- Spring Boot 3
- Maven
- JUnit 5
- AssertJ
- MockMvc

---
## Requisitos do projeto
Esta seção acompanha o progresso de implementação dos requisitos funcionais,
não funcionais e diferenciais definidos para o `speed-violation-service`.

## Requisitos

### Legenda

| Ícone | Status |
|:---:|---|
| ✅ | Concluído |
| 🕒 | Em andamento |

### Requisitos Funcionais

- ✅ **RF1 — Apuração de velocidade**
  - Endpoint `POST /api/v1/violations/evaluate`.
  - Receber a leitura e retornar o resultado da apuração.

- ✅ **RF2 — Validação de entrada**
  - Validar placa, velocidades, equipamento, timestamp e header `x-origin`.
  - Retornar `400 Bad Request` para entradas inválidas.

- ✅ **RF3 — Regras de apuração**
  - Aplicar margem de tolerância.
  - Calcular percentual de excesso.
  - Classificar a infração como `MEDIUM`, `SERIOUS` ou `VERY_SERIOUS`.

- 🕒 **RF4 — Persistência e consulta**
  - Armazenar somente infrações em memória considerando acesso concorrente.
  - Consultar infrações por placa através de `GET /api/v1/violations`.

- ✅ **RF5 — Tratamento de erros**
  - Padronizar respostas de erro.
  - Centralizar o tratamento de exceções.
  - Registrar logs de validação e erros inesperados.
  - Não expor detalhes internos da aplicação ao cliente.

- ✅ **RF6 — Casos especiais**
  - Tratar corretamente margens de tolerância, formatos de placa,
    limites de 20% e 50%, vias acima de 100 km/h e timestamps futuros.

### Requisitos Não Funcionais

- 🕒 **RNF1 — Organização do código**
  - Separar responsabilidades e documentar as decisões arquiteturais.

- ✅ **RNF2 — Configuração externalizada**
  - Externalizar porta e parâmetros das margens de tolerância.

- 🕒 **RNF3 — Testes**
  - Testar regras de negócio, validações e casos de fronteira.
  - Manter cobertura mínima de 80% da camada de negócio.

- 🕒 **RNF4 — Documentação**
  - Documentar execução, testes, exemplos de uso e decisões técnicas.

- 🕒 **RNF5 — Qualidade de código**
  - Utilizar Java 21, código limpo, Records quando apropriado e boas práticas.

### Diferenciais

- 🕒 Swagger / OpenAPI
- 🕒 Dockerfile
- 🕒 Testes de integração
- 🕒 Collection Postman / Insomnia
- 🕒 Pipeline CI
- 🕒 Aplicação hospedada

---

## Arquitetura

O projeto adota uma organização **package by feature**, concentrando a
funcionalidade de apuração em `features/violation`.

Dentro da feature, a aplicação segue uma separação em camadas com nomes
convencionais do ecossistema Spring:

```text
features/
└── violation/
    ├── controller/
    ├── dto/
    ├── exception/
    ├── model/
    ├── provider/
    ├── repository/
    ├── service/
    └── validator/
```

### Decisões arquiteturais

- **Package by feature:** mantém os componentes relacionados à infração agrupados
  em `features/violation`, facilitando manutenção e evolução.
- **Arquitetura em camadas:** controller, service, repository e provider possuem
  responsabilidades distintas.
- **Controller:** responsável pelo contrato HTTP e pela conversão entre DTOs e
  modelos utilizados pela aplicação.
- **DTOs:** representam os contratos de entrada e saída da API, evitando expor
  diretamente os modelos internos.
- **Validação de entrada:** utiliza Jakarta Bean Validation nos DTOs para regras
  simples e uma annotation customizada `@ValidLicensePlate` para validação dos
  formatos de placa.
- **Validação de placa:** as expressões regulares dos formatos antigo e Mercosul
  são compiladas como constantes estáticas no validator.
- **Header de origem:** `x-origin` é representado pelo enum `CaptureOrigin`,
  restringindo os valores aceitos a `FIXED`, `MOBILE` e `HANDHELD`.
- **Tratamento centralizado de erros:** utiliza `@RestControllerAdvice` para
  padronizar respostas de validação e falhas inesperadas.
- **Respostas de erro seguras:** detalhes internos e stack traces permanecem nos
  logs da aplicação e não são enviados ao cliente.
- **Regras de negócio no service:** cálculos de tolerância, percentual de excesso
  e classificação da infração permanecem no `ViolationService`.
- **Repository Pattern:** o acesso aos dados é definido através de uma abstração,
  evitando dependência direta do mecanismo de armazenamento.
- **Provider de persistência:** a implementação atual utiliza armazenamento em
  memória e pode ser substituída por outro provider sem alterar o contrato do repository.
- **Persistência condicionada:** somente avaliações que resultam em infração são
  enviadas ao repository.
- **Acesso concorrente:** o armazenamento em memória utiliza estruturas concorrentes
  para suportar múltiplos acessos de forma segura.
- **Modelos imutáveis:** Records são utilizados quando apropriado.
- **Precisão no percentual:** `BigDecimal` é utilizado para evitar imprecisões de
  ponto flutuante.
- **Classificação tipada:** a severidade da infração é representada por
  `ViolationSeverity`.
- **Configuração externalizada:** parâmetros operacionais são definidos através
  do `application.properties`.
- **Desenvolvimento orientado a testes:** o projeto utiliza TDD como apoio ao
  desenvolvimento das regras e funcionalidades.

---
## Configuração

As configurações operacionais da aplicação são definidas em
`application.properties`.

| Propriedade                                | Valor padrão |
| ------------------------------------------ | -----------: |
| `server.port`                              |       `8080` |
| `violation.tolerance.fixed-kmh`            |          `7` |
| `violation.tolerance.percentage`           |          `7` |
| `violation.tolerance.percentage-threshold` |        `100` |

Os valores podem ser sobrescritos por variáveis de ambiente, permitindo ajustar
a aplicação entre diferentes ambientes sem alterar o código-fonte.

---

## Pré-requisitos

Para executar o projeto localmente é necessário:

- Java 21
- Git

---

## Running the application

Clone o repositório e acesse o diretório do projeto.

No Windows:

```bash
.\mvnw.cmd spring-boot:run
```

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

Por padrão, a aplicação estará disponível em:

`http://localhost:8080`

A porta pode ser alterada através da variável de ambiente `SERVER_PORT`.

---

## API

### Avaliar velocidade

```http
POST /api/v1/violations/evaluate
```

Header obrigatório:

```text
x-origin: FIXED
```

Os valores aceitos para `x-origin` são:

- `FIXED`
- `MOBILE`
- `HANDHELD`

Formatos de placa aceitos:

- Antigo: `ABC1234`
- Mercosul: `ABC1D23`

### Exemplo com infração

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

Exemplo de resposta:

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

### Exemplo sem infração

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

Exemplo de resposta:

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

### Exemplo de erro de validação

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

Exemplo de resposta:

```json
{
  "error": "INVALID_LICENSE_PLATE",
  "message": "Invalid license plate format",
  "timestamp": "2026-08-12T22:00:00Z"
}
```

Erros inesperados retornam `500 Internal Server Error` com mensagem genérica,
sem exposição de stack trace ou detalhes internos da aplicação.

---
## API Collection

Uma collection do Insomnia está disponível em:

`insomnia/speed-violation-service.yaml`

Atualmente, a collection contém exemplos para o endpoint
`POST /api/v1/violations/evaluate`, incluindo cenários com e sem infração.

A URL base utilizada no ambiente local é:

`http://localhost:8080`

---

## Tests

O projeto possui testes unitários para regras de negócio, persistência em memória
e expressões regulares de validação de placa.

Também possui testes de integração do endpoint
`POST /api/v1/violations/evaluate` utilizando Spring Boot e MockMvc, cobrindo:

- cenários com e sem infração;
- validações de placa, velocidades, equipamento e timestamp;
- validação do header `x-origin`;
- respostas de erro padronizadas;
- erros inesperados `500` sem exposição de detalhes internos.

Para executar todos os testes:

No Windows:

```bash
.\mvnw.cmd test
```

No Linux/macOS:

```bash
./mvnw test
```