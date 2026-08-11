# Speed Violation Service

REST microservice responsible for processing speed readings captured by
traffic enforcement equipment and evaluating speed violations.

---

## Technologies

- Java 21
- Spring Boot 3
- Maven

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

- 🕒 **RF3 — Regras de apuração**
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

## Architecture

Documentation in progress.

## Running the application

Documentation in progress.

## Tests

Documentation in progress.