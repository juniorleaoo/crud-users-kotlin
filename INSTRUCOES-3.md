## Terceira etapa

### 1. Implementar um CRUD de vagas de emprego

Nesse CRUD será possível cadastrar vagas de emprego. Informando o nome da vaga e os requisitos da vaga.

| Atributo     | descrição                                                                                        |
|--------------|--------------------------------------------------------------------------------------------------|
| name         | nome da vaga, obrigatório, máximo 500 caracteres, não é permitido valores nulos e vazio          |
| description  | descrição da vaga, opcional, não tem limite de caracteres, não é permitido valores nulos e vazio |
| salary       | salário da vaga, obrigatório, não é permitido valores nulos e vazio                              |
| requirements | lista de requisitos, obrigatório, [Requirement](#objeto-requirements)                            |

#### Objeto Requirements

| Atributo | descrição                                                                               |
|----------|-----------------------------------------------------------------------------------------|
| stack    | nome da vaga, obrigatório, máximo 500 caracteres, não é permitido valores nulos e vazio |
| level    | objeto que contém `min` e `max` requerido da vaga, opcional                             |

```json
{
  "name": "Desenvolvedor Java Jr.",
  "description": "",
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

- `GET /jobs/{id}/match` - Retorna uma lista de usuários que atende aos requisitos da vaga

### 3. Implementar um endpoint de match onde retorne as vagas que o usuário atende os requisitos

- `GET /users/{id}/jobs/match` - Retorna uma lista de vagas que o usuário atende os requisitos
