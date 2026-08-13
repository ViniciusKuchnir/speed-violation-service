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
- JaCoCo
- Springdoc OpenAPI / Swagger UI
- Docker
- GitHub Actions
- Render

---
## Requisitos do projeto
Esta seção acompanha o progresso de implementação dos requisitos funcionais,
não funcionais e diferenciais definidos para o `speed-violation-service`.

## Requisitos

### Legenda

| Ícone | Status       |
| :---: | ------------ |
|   ✅   | Concluído    |
|   🕒  | Em andamento |

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

- ✅ **RF4 — Persistência e consulta**
  - Armazenar somente infrações em memória considerando acesso concorrente.
  - Consultar infrações por placa através de `GET /api/v1/violations`.
  - Retornar lista vazia quando não houver infrações registradas.

- ✅ **RF5 — Tratamento de erros**
  - Padronizar respostas de erro.
  - Centralizar o tratamento de exceções.
  - Registrar logs de validação e erros inesperados.
  - Não expor detalhes internos da aplicação ao cliente.

- ✅ **RF6 — Casos especiais**
  - Tratar corretamente margens de tolerância, formatos de placa,
    limites de 20% e 50%, vias acima de 100 km/h e timestamps futuros.

### Requisitos Não Funcionais

- ✅ **RNF1 — Organização do código**
  - Separar responsabilidades e documentar as decisões arquiteturais.

- ✅ **RNF2 — Configuração externalizada**
  - Externalizar porta e parâmetros das margens de tolerância.

- ✅ **RNF3 — Testes**
  - Testar regras de negócio, validações e casos de fronteira.
  - Manter cobertura mínima de 80% da camada de negócio.
  - Validar automaticamente a cobertura através do JaCoCo.

- ✅ **RNF4 — Documentação**
  - Documentar execução, testes, exemplos de uso e decisões técnicas.
  - Disponibilizar documentação interativa da API através de OpenAPI.

- ✅ **RNF5 — Qualidade de código**
  - Utilizar Java 21, código limpo, Records quando apropriado e boas práticas.

### Diferenciais

- ✅ Swagger / OpenAPI
- ✅ Dockerfile
- ✅ Testes de integração
- ✅ Collection Postman / Insomnia
- ✅ Pipeline CI
- ✅ Aplicação hospedada

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
    ├── mapper/
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
- **Controller:** responsável pelo contrato HTTP e pela orquestração das chamadas
  à camada de serviço.
- **DTOs:** representam os contratos de entrada e saída da API, evitando expor
  diretamente os modelos internos.
- **Mapper:** centraliza a conversão entre modelos internos e DTOs de resposta,
  evitando duplicação entre endpoints.
- **Validação de entrada:** utiliza Jakarta Bean Validation nos DTOs para regras
  simples e uma annotation customizada `@ValidLicensePlate` para validação dos
  formatos de placa.
- **Validação de placa:** as expressões regulares dos formatos antigo e Mercosul
  são compiladas como constantes estáticas no validator.
- **Header de origem:** `x-origin` é representado por enum, restringindo os valores
  aceitos a `FIXED`, `MOBILE` e `HANDHELD`.
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
- **Cobertura automatizada:** o JaCoCo verifica a cobertura da camada de negócio
  durante a fase `verify` do Maven.
- **Documentação OpenAPI:** contratos, parâmetros, schemas e respostas da API são
  documentados através do Springdoc OpenAPI e disponibilizados pelo Swagger UI.
- **Containerização:** a aplicação utiliza Docker com multi-stage build em Java 21,
  separando o ambiente de compilação da imagem utilizada em execução.
- **Integração contínua:** o GitHub Actions executa automaticamente build, testes,
  validação de cobertura e build da imagem Docker em pushes e pull requests.
- **Deploy:** a aplicação é disponibilizada publicamente através do Render,
  utilizando a imagem construída a partir do `Dockerfile` do projeto.

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

Para execução via container:

- Docker

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
## Docker

A aplicação possui um `Dockerfile` multi-stage utilizando Java 21.

O estágio de build utiliza o Maven Wrapper do projeto para gerar o artefato da
aplicação, enquanto a imagem final contém apenas o runtime necessário para
executar o arquivo JAR.

### Build da imagem

Na raiz do projeto:

```bash
docker build -t speed-violation-service .
```

### Executar o container

Utilizando a porta padrão `8080`:

```bash
docker run --name speed-violation-service --rm -p 8080:8080 speed-violation-service
```

A aplicação estará disponível em:

`http://localhost:8080`

### Alterar a porta

A porta pode ser sobrescrita através da variável de ambiente `SERVER_PORT`.

Exemplo utilizando a porta `9090`:

```bash
docker run --name speed-violation-service --rm -e SERVER_PORT=9090 -p 9090:9090 speed-violation-service
```

A aplicação estará disponível em:

`http://localhost:9090`

### Health check

Com a aplicação em execução:

```http
GET /actuator/health
```

Exemplo:

```bash
curl http://localhost:8080/actuator/health
```

Exemplo de resposta:

```json
{
  "status": "UP",
  "groups": [
    "liveness",
    "readiness"
  ]
}
```

O campo `status` com valor `UP` indica que a aplicação está disponível.

A persistência da aplicação continua sendo realizada somente em memória.
Portanto, os registros armazenados são perdidos quando o container é encerrado.

---

## Deployment

A aplicação está hospedada publicamente no **Render** utilizando o `Dockerfile`
do projeto.

### Ambiente público

| Recurso | URL |
| ------- | --- |
| API | `https://speed-violation-service-cgk1.onrender.com` |
| Swagger UI | `https://speed-violation-service-cgk1.onrender.com/swagger-ui.html` |
| OpenAPI JSON | `https://speed-violation-service-cgk1.onrender.com/v3/api-docs` |
| Health Check | `https://speed-violation-service-cgk1.onrender.com/actuator/health` |

O ambiente hospedado utiliza a mesma aplicação containerizada utilizada na
execução local, com configurações operacionais fornecidas através de variáveis
de ambiente.

O endpoint utilizado para verificação de saúde da aplicação é:

```http
GET /actuator/health
```

Uma aplicação disponível retorna `status` igual a `UP`.

### Observação sobre o ambiente gratuito

A aplicação está hospedada utilizando uma instância gratuita do Render.

Serviços gratuitos podem entrar em estado de inatividade após aproximadamente
15 minutos sem receber tráfego. Quando isso ocorre, a primeira requisição
seguinte inicia novamente a instância e pode levar cerca de um minuto para ser
respondida.

Portanto, caso o primeiro acesso à API ou ao Swagger apresente um tempo de
resposta maior, aguarde a inicialização da instância e tente novamente.

### Persistência no ambiente hospedado

A persistência exigida pelo projeto é exclusivamente em memória.

Consequentemente, as infrações registradas existem somente durante o ciclo de
vida da instância atual da aplicação. Reinicializações, novos deploys ou
recriações da instância resultam em um armazenamento em memória vazio.

Esse comportamento é intencional e segue o requisito de persistência em memória
definido para o projeto.

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

### Consultar infrações por placa
```http
GET /api/v1/violations?licensePlate=ABC1D23
```

Exemplo:

```bash
curl "http://localhost:8080/api/v1/violations?licensePlate=ABC1D23"
```

Exemplo de resposta:

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

A persistência é realizada somente em memória. Portanto, os registros são
removidos quando a aplicação é reiniciada.

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
## OpenAPI / Swagger

A API possui documentação OpenAPI gerada através do Springdoc e uma interface
interativa disponibilizada pelo Swagger UI.

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

A documentação descreve:

- `POST /api/v1/violations/evaluate`;
- `GET /api/v1/violations`;
- header obrigatório `x-origin`;
- parâmetros de entrada;
- schemas de request e response;
- valores aceitos para enums;
- respostas `200`, `400` e `500`;
- formato padronizado das respostas de erro.

O Swagger UI também permite executar os endpoints diretamente pela interface.

---
## API Collection

Uma collection do Insomnia está disponível em:

`insomnia/speed-violation-service.yaml`

A collection contém exemplos para:

- avaliação com infração;
- avaliação sem infração;
- consulta de infrações por placa;
- consulta de placa sem infrações;
- consulta com placa inválida;
- health check da aplicação.

A URL base utilizada no ambiente local é:

`http://localhost:8080`

Para testar o ambiente hospedado, a URL base pode ser substituída por:

`https://speed-violation-service-cgk1.onrender.com`

---
## Continuous Integration

O projeto utiliza GitHub Actions para executar automaticamente o processo de
integração contínua em pushes e pull requests.

O workflow está definido em:

`.github/workflows/ci.yml`

O pipeline utiliza um runner Linux e executa as seguintes etapas:

1. checkout do repositório;
2. configuração do Java 21;
3. configuração do Maven Wrapper;
4. execução do `mvn clean verify`;
5. execução dos testes automatizados;
6. validação da cobertura mínima através do JaCoCo;
7. build da imagem Docker.

A execução é considerada bem-sucedida somente quando todas as etapas são
concluídas sem erros.

O processo garante que alterações integradas ao repositório continuem
compilando, atendam à cobertura mínima de testes e mantenham uma imagem Docker
válida.

---

## Future Improvements

A implementação atual foi mantida intencionalmente compatível com o escopo do
desafio. Em um cenário real de produção, decisões relacionadas a escalabilidade
e arquitetura deveriam ser tomadas com base no comportamento observado da
aplicação e em métricas de utilização.

### Load Testing

Antes de introduzir mudanças arquiteturais voltadas à escalabilidade, seriam
realizados testes de carga para compreender o comportamento da aplicação sob
diferentes volumes de requisições.

Esses testes permitiriam avaliar principalmente o throughput das avaliações de
velocidade, o tempo de resposta dos endpoints, o comportamento da aplicação em
picos de utilização e possíveis gargalos.

Também permitiriam validar uma hipótese derivada do próprio domínio: o volume de
avaliações de velocidade pode ser significativamente superior ao volume de
consultas por placa, já que cada passagem de um veículo por um equipamento pode
originar uma avaliação, enquanto consultas tendem a ocorrer com menor frequência.

Essa hipótese não é assumida como uma característica garantida do sistema. Os
resultados dos testes de carga e, posteriormente, métricas reais de produção
seriam utilizados como evidência para orientar decisões de arquitetura.

### CQRS

Caso os testes de carga e as métricas de produção confirmassem uma diferença
significativa entre os padrões de escrita e leitura, uma possível evolução seria
a adoção de CQRS (Command Query Responsibility Segregation).

Nesse cenário:

`POST /api/v1/violations/evaluate`

representaria o fluxo de comandos, responsável pelo processamento das avaliações
e registro das infrações.

Enquanto:

`GET /api/v1/violations`

representaria o fluxo de consultas.

A separação permitiria que os caminhos de escrita e leitura fossem evoluídos,
otimizados e escalados de forma independente caso apresentassem necessidades
operacionais diferentes.

CQRS não foi aplicado na implementação atual porque não existem métricas que
demonstrem essa necessidade. Introduzir o padrão antecipadamente adicionaria
complexidade sem uma evidência concreta de benefício.

A decisão, portanto, partiria primeiro de uma hipótese baseada no domínio,
seguida por testes de carga e, em um ambiente real, métricas de produção.

### Idempotency

Em um ambiente real, equipamentos de fiscalização ou serviços responsáveis pelo
envio das leituras podem repetir uma requisição em situações como timeout,
instabilidade de rede ou mecanismos automáticos de retry.

Sem controle de idempotência, uma mesma captura poderia ser processada mais de
uma vez e potencialmente gerar registros duplicados de uma mesma infração.

Uma evolução da API seria associar cada captura a um identificador único. Dessa
forma, o serviço poderia reconhecer uma avaliação já processada e impedir que a
mesma leitura produzisse uma nova infração em caso de reenvio.

Essa característica se tornaria ainda mais importante caso a aplicação evoluísse
para processamento distribuído ou assíncrono, onde duplicidade de mensagens e
reprocessamentos devem ser considerados como parte do funcionamento do sistema.

---

## Commit Convention

O histórico do projeto foi organizado utilizando commits pequenos e focados em
uma única responsabilidade.

As mensagens seguem um padrão inspirado em **Conventional Commits**:

```text
<type>: <description>
```

Os principais tipos utilizados no projeto são:

| Tipo       | Utilização |
| ---------- | ---------- |
| `feat`     | Implementação de novas funcionalidades |
| `test`     | Criação ou alteração de testes |
| `refactor` | Alterações estruturais sem mudança de comportamento |
| `build`    | Configurações relacionadas ao build, dependências ou Docker |
| `ci`       | Configurações de integração contínua |
| `docs`     | Alterações de documentação, OpenAPI ou collections da API |

As descrições são mantidas curtas, em inglês e identificam diretamente a
responsabilidade da alteração.

Exemplos do padrão utilizado:

```text
feat: add violation query endpoint
test: add violation query integration tests
refactor: reuse violation response mapper
build: add Docker image configuration
ci: add build and test workflow
docs: document hosted application
```

A separação dos commits por responsabilidade facilita a revisão do histórico,
a identificação das decisões tomadas durante o desenvolvimento e a análise
isolada de cada alteração.

---

## Tests

O projeto possui testes unitários para regras de negócio, persistência em memória
e expressões regulares de validação de placa.

Também possui testes de integração utilizando Spring Boot e MockMvc, cobrindo:

- avaliação com e sem infração;
- validações de placa, velocidades, equipamento e timestamp;
- validação do header `x-origin`;
- respostas de erro padronizadas;
- erros inesperados `500` sem exposição de detalhes internos;
- consulta de infrações por placa;
- retorno de lista vazia quando não existem registros;
- garantia de que avaliações sem infração não são persistidas.

### Cobertura de testes

A cobertura é analisada através do JaCoCo e validada durante a fase `verify`
do Maven.

A camada de negócio possui cobertura superior ao mínimo de 80% definido para
o projeto.

Evidência da execução atual:

| Escopo             | Cobertura de linhas | Cobertura de branches |
| ------------------ | -------------------: | ---------------------: |
| `ViolationService` |         100% (46/46) |           100% (14/14) |
| Projeto completo   |       97,9% (184/188) |            90,9% (40/44) |

O build falha caso a cobertura de linhas da camada de negócio fique abaixo de
80%.

Para executar somente os testes:

No Windows:

```bash
.\mvnw.cmd test
```

No Linux/macOS:

```bash
./mvnw test
```

Para executar os testes e validar a cobertura:

No Windows:

```bash
.\mvnw.cmd clean verify
```

No Linux/macOS:

```bash
./mvnw clean verify
```

O relatório HTML de cobertura é gerado em:

`target/site/jacoco/index.html`