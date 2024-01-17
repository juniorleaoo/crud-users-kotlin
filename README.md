## CRUD de usuário

### Atributos

| atributo             | descrição                                                                   |
|----------------------|-----------------------------------------------------------------------------|
| id                   | obrigatório, UUID                                                           |
| apelido              | opcional, máximo 32 caracteres                                              |
| nome                 | obrigatório, máximo 255 caracteres                                          |
| data de nascimento   | obrigatório, formato ISO8601                                                |
| lista de tecnologias | optional, lista de string com cada valor obrigatório e de até 32 caracteres |


### Endpoints

- [x] `GET /users/{id}`
- [x] `GET /users`
- [x] `DELETE /users/{id}`
- [x] `POST /users`
- [x] `PUT /users/{id}`

```json
{
  "nick": "Fulano",
  "name": "João do Alface",
  "birth_date": "2000-01-01T20:20:03Z",
  "stack": ["Java", "NodeJS"]
}
```