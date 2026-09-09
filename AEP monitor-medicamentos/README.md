# Monitor de Medicamentos

## Problema
Pacientes em tratamento contínuo — especialmente idosos e pessoas com doenças
crônicas — frequentemente esquecem horários de medicação ou perdem o controle
sobre quais remédios já tomaram no dia. Isso pode levar a doses duplicadas,
esquecimentos perigosos e piora do quadro de saúde.

## ODS relacionado
**ODS 3 — Saúde e Bem-Estar**: a solução contribui para garantir vidas
saudáveis e promover o bem-estar, ajudando pacientes e cuidadores a
gerenciar tratamentos medicamentosos de forma organizada.

## Público-alvo
Pacientes em tratamento contínuo e seus cuidadores (familiares ou
profissionais de saúde domiciliar).

## Tecnologias utilizadas
- **Java 17**
- **Spring Boot 3** (`spring-boot-starter-web`, `spring-boot-starter-data-mongodb`,
  `spring-boot-starter-validation`)
- **MongoDB** (banco de dados NoSQL, rodando em container Docker)
- **springdoc-openapi** (Swagger UI gerado a partir dos controllers REST)
- **Maven** (gerenciamento de dependências e build)
- **JUnit 5** + **Mockito** (testes automatizados)
- **JaCoCo** (relatório de cobertura de testes, com verificação automática de mínimo)
- **GitHub Actions** (CI: roda os testes e valida a cobertura a cada push/PR)

## Arquitetura
O código é organizado em camadas, para manter responsabilidades separadas:

```
controller/  → mapeia rotas HTTP para chamadas de serviço (sem lógica de negócio)
service/     → regras de negócio (buscar, validar existência, atualizar estado)
repository/  → acesso ao MongoDB (Spring Data)
model/       → entidade persistida na coleção "medicamentos"
dto/         → dados de entrada para operações específicas (ex: atualizar horário)
exception/   → exceções de domínio + tratamento centralizado de erros da API
```

Quando um medicamento não é encontrado, o service lança
`MedicamentoNaoEncontradoException`, que é convertida automaticamente pelo
`GlobalExceptionHandler` em uma resposta `404` estruturada, por exemplo:

```json
{
  "timestamp": "2026-09-08T13:00:00Z",
  "status": 404,
  "erro": "Recurso não encontrado",
  "mensagem": "Medicamento não encontrado com id: 999"
}
```

Erros de validação de campos (`@Valid`) também são centralizados e retornam
um corpo simples de `campo → mensagem`, por exemplo:

```json
{
  "horario": "Horário deve estar no formato HH:mm (ex: 08:00)"
}
```

## Funcionalidades da 1ª entrega
A API expõe os seguintes endpoints REST em `/medicamentos`, testáveis pelo Swagger UI:

| Verbo | Rota | Ação |
|---|---|---|
| POST | `/medicamentos` | Cadastrar medicamento |
| GET | `/medicamentos` | Listar todos |
| GET | `/medicamentos/paciente/{nomePaciente}` | Listar medicamentos de um paciente |
| GET | `/medicamentos/{id}` | Buscar por id |
| PATCH | `/medicamentos/{id}/horario` | Atualizar horário |
| PATCH | `/medicamentos/{id}/tomado` | Marcar como tomado |
| DELETE | `/medicamentos/{id}` | Remover |

O campo `horario` é validado no formato `HH:mm` (ex: `08:00`, `23:59`); valores
fora desse padrão são rejeitados com `400` antes de chegar ao banco.

## Como executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- Docker (para rodar o MongoDB em container)

### Passo a passo
1. Subir o MongoDB via Docker Compose:
   ```bash
   docker compose up -d
   ```
2. Subir a API:
   ```bash
   mvn spring-boot:run
   ```
3. Acessar `http://localhost:8080/swagger-ui.html` e testar todos os endpoints
   de `/medicamentos` diretamente pela interface do Swagger.

Por padrão a aplicação conecta em `mongodb://localhost:27017/monitor_medicamentos`.
Para usar outra instância (ex: MongoDB Atlas), defina a variável de ambiente
`MONGO_URI` antes de subir a aplicação.

## Como rodar os testes e gerar a cobertura
Para rodar os testes e gerar o relatório de cobertura, sem travar o build:
```bash
mvn test
```
O relatório é gerado em:
```
target/site/jacoco/index.html
```

Para validar automaticamente a cobertura mínima exigida (**70%**), rode:
```bash
mvn verify
```
Esse comando **falha o build** se a cobertura ficar abaixo de 70% — é a
evidência reproduzível de cobertura pedida no enunciado da AEP. O mesmo
comando roda automaticamente a cada push/PR via GitHub Actions
(`.github/workflows/ci.yml`).

## Estrutura do banco (1º semestre)
Coleção única `medicamentos`, com documentos simples e homogêneos:

```json
{
  "nomePaciente": "Maria",
  "nomeMedicamento": "Dipirona",
  "dosagem": "500mg",
  "horario": "08:00",
  "tomado": false
}
```

A coleção é criada explicitamente com validação de schema (`$jsonSchema`) pelo
script [`mongo-init/init.js`](mongo-init/init.js), executado automaticamente
pelo MongoDB na primeira vez que o container sobe (via
`docker-entrypoint-initdb.d`, configurado no `docker-compose.yml`). Isso
garante que só documentos com os campos obrigatórios corretos sejam aceitos
no banco. Há também um índice em `nomePaciente`, usado pelo endpoint de
listagem por paciente.

Para conferir a coleção e a validação direto no Mongo:
```bash
docker exec -it mongo-monitor mongosh monitor_medicamentos --eval "db.getCollectionInfos()"
```

> Se o container do Mongo já foi criado antes desse script existir, o volume
> de dados (`mongo-data`) já está inicializado e o script não roda de novo
> automaticamente. Para forçar a recriação com o script:
> ```bash
> docker compose down -v
> docker compose up -d
> ```
> (isso apaga os dados de teste já cadastrados no Mongo local — não afeta o código).

## Estrutura do projeto
```
monitor-medicamentos/
├── .github/workflows/ci.yml
├── src/main/java/com/aep/monitor/
│   ├── MonitorMedicamentosApplication.java
│   ├── model/Medicamento.java
│   ├── repository/MedicamentoRepository.java
│   ├── service/MedicamentoService.java
│   ├── controller/MedicamentoController.java
│   ├── dto/AtualizarHorarioRequest.java
│   └── exception/
│       ├── MedicamentoNaoEncontradoException.java
│       └── GlobalExceptionHandler.java
├── src/main/resources/application.properties
├── src/test/java/com/aep/monitor/
│   ├── MedicamentoTest.java
│   ├── controller/MedicamentoControllerTest.java
│   └── service/MedicamentoServiceTest.java
├── mongo-init/init.js
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Próximos passos (2ª entrega)
- Evoluir para múltiplas coleções (ex: pacientes relacionados aos medicamentos)
- Adicionar histórico de doses tomadas (subdocumentos)
- Ampliar testes e documentação técnica completa
