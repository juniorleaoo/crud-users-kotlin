## Terceira etapa

### 1. Implementar um CRUD de vagas de emprego

Nesse CRUD será possível cadastrar vagas de emprego. Informando o nome da vaga e os requisitos da vaga.

| Atributo     | descrição                                                                                                |
|--------------|----------------------------------------------------------------------------------------------------------|
| name         | nome da vaga, obrigatório, máximo 500 caracteres, não é permitido valores nulos e vazio                  |
| description  | descrição da vaga, opcional, não tem limite de caracteres, não é permitido valores nulos e vazio         |
| salary       | salário da vaga, obrigatório, não é permitido valores nulos e vazio (O valor é representado em centavos) |
| requirements | lista de requisitos, obrigatório, [Requirement](#objeto-requirements)                                    |

#### Objeto Requirements

| Atributo | descrição                                                                                                     |
|----------|---------------------------------------------------------------------------------------------------------------|
| stack    | nome da stack, string obrigatório, não é permitido valores nulos e vazios, máximo 32 caracteres, valor único  |
| level    | objeto que contém `min` e `max` requerido da vaga, opcional                                                   |

```json
{
  "name": "Desenvolvedor Java Jr.",
  "description": "",
  "salary": 100000,
  "requirements": [
    {
      "stack": "Java",
      "level": {
        "min": 10,
        "max": 90
      }
    },
    {
      "stack": "Kotlin",
      "level": {
        "min": 50
      }
    }
  ]
}
```

#### Endpoints

A API precisa expor os seguintes endpoints:

- `GET /jobs` - Retorna uma lista de vagas de emprego
- `GET /jobs/{id}` - Retorna uma vaga de emprego
- `POST /jobs` - Cria uma vaga de emprego
- `PUT /jobs/{id}` - Altera uma vaga de emprego
- `DELETE /jobs/{id}` - Deleta uma vaga de emprego

### 2. Implementar um endpoint de match onde retorne os usuários que atendem os requisitos da vaga

- `GET /jobs/{id}/match?page_size=30&page=3&sort=-name` - Retorna uma lista de usuários que atende aos requisitos da vaga
```json
{
  "records": [
    {
      "id": "123",
      "nick": "Fulano",
      "name": "João do Alface",
      "birth_date": "2000-01-01T20:20:03Z",
      "stack": [
        {
          "name": "Java",
          "level": 50
        },
        {
          "name": "Kotlin",
          "level": 50
        }
      ]
    }
  ],
  "page": 0,
  //página atual
  "page_size": 15,
  //quantidade de itens por página
  "total": 5000
  //total de usuários que tem na base
}
```

### 3. Implementar um endpoint de match onde retorne as vagas que o usuário atende os requisitos

- `GET /users/{id}/jobs/match?page_size=30&page=3&sort=-name` - Retorna uma lista de vagas que o usuário atende os requisitos
```json
{
  "records": [
    {
      "id": "456",
      "name": "Desenvolvedor Java Jr.",
      "description": "",
      "salary": 100000,
      "requirements": [
        {
          "stack": "Java",
          "level": {
            "min": 10,
            "max": 90
          }
        },
        {
          "stack": "Kotlin",
          "level": {
            "min": 50
          }
        }
      ]
    }
  ],
  "page": 0,
  //página atual
  "page_size": 15,
  //quantidade de itens por página
  "total": 5000
  //total de usuários que tem na base
}
```

### 4. Criar uma nova entidade chamada "Entrevista"

Essa entidade será responsavel pala conexão entre uma vaga e um usuário (candidato).

| Atributo       | descrição                                      |
|----------------|------------------------------------------------|
| user_id        | id do usuário selecionado para a vaga          |
| job_id         | id da vaga de emprego                          |
| created_at     | data de criação da entrevista (ISO8601)        |
| interview_date | data que a entrevista será realizada (ISO8601) |

### 5. Implementar um endpoint de criação de entrevista

- `POST /interview` - Realiza a crição de uma entrevista e faz o envio para o tópico do Kafka
```json
{
  "user_id": "123",
  "job_id": "456",
  "interview_date": "2024-03-01T12:00:00Z"
}
```

### 6. Integre com Kafka e AVRO

Quando o método POST for chamado, uma mensagem deve ser enviada para um tópico do Kafka chamado `interview.notification`. A mensagem deve ser um objeto AVRO que representa a “Entrevista” que foi criada.

| Atributo       | descrição                                      |
|----------------|------------------------------------------------|
| interview_id   | id da entrevista                               |
| interview_date | data que a entrevista será realizada (ISO8601) |
| user_id        | id do usuário selecionado para a vaga          |
| user_name      | nome do usuário                                |
| job_id         | id da vaga de emprego                          |
| job_name       | nome da vaga                                   |

### 7. Implementar testes unitários e de integração

Implementar os testes unitários e de integração, seguindo o que já foi realizada nas outras instruções.