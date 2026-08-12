# Speed Violation Service

Microserviço REST responsável por processar leituras de velocidade captadas por equipamentos de fiscalização de trânsito, aplicar as regras de tolerância e identificar possíveis infrações por excesso de velocidade.

---

## Technologies

- Java 21
- Spring Boot 3
- Maven
- JUnit 5
- AssertJ

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

- 🕒 **RF1 — Apuração de velocidade**
    - Endpoint `POST /api/v1/violations/evaluate`
    - Receber a leitura e retornar o resultado da apuração.

- 🕒 **RF2 — Validação de entrada**
    - Validar placa, velocidades, equipamento, timestamp e header `x-origin`.

- ✅ **RF3 — Regras de apuração**
    - Aplicar margem de tolerância.
    - Calcular percentual de excesso.
    - Classificar a infração como `MEDIUM`, `SERIOUS` ou `VERY_SERIOUS`.

- 🕒 **RF4 — Persistência e consulta**
    - Armazenar infrações em memória considerando acesso concorrente.
    - Consultar infrações por placa através de `GET /api/v1/violations`.

- 🕒 **RF5 — Tratamento de erros**
    - Padronizar respostas de erro.
    - Centralizar exceções e registrar logs adequadamente.

- 🕒 **RF6 — Casos especiais**
    - Tratar corretamente margens de tolerância, formatos de placa,
      limites de 20% e 50%, vias acima de 100 km/h e timestamps futuros.

### Requisitos Não Funcionais

- 🕒 **RNF1 — Organização do código**
    - Separar responsabilidades e documentar as decisões arquiteturais.

- 🕒 **RNF2 — Configuração externalizada**
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
    ├── model/
    ├── repository/
    ├── service/
    └── validator/
```

### Decisões arquiteturais

- **Package by feature:** mantém os componentes relacionados à infração agrupados
  em `features/violation`, facilitando a navegação, a manutenção do código e possíveis novos módulos futuros.
- **Arquitetura em camadas:** `controller`, `service` e `repository` possuem
  responsabilidades distintas, evitando concentrar toda a lógica em uma única camada.
- **Regras de negócio no service:** cálculos de tolerância, percentual de excesso
  e classificação da infração ficam no `ViolationService`. Validators ficam
  reservados para validações de entrada.
- **Modelos simples e imutáveis:** Records são utilizados quando apropriado para
  representar os dados da apuração.
- **Precisão no percentual:** `BigDecimal` é utilizado no cálculo do percentual de
  excesso para evitar imprecisões de ponto flutuante.
- **Classificação tipada:** a severidade da infração é representada por
  `ViolationSeverity`, evitando valores de classificação espalhados como strings.
- **Desenvolvimento orientado a testes:** o projeto utiliza TDD como apoio ao desenvolvimento, priorizando testes de comportamento e regras de negócio antes da implementação das funcionalidades.
---

## Running the application

Documentation in progress.

## Tests

Para executar os testes:

No Windows:

```bash
.\mvnw.cmd test
```

No Linux/macOS:

```bash
./mvnw test
```