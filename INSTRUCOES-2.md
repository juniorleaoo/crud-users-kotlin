## Segunda etapa

Agora que temos um CRUD de usuário simples, iremos

1. [Adicionar paginação e ordenação na listagem de usuários](#1-adicionar-paginação-e-ordenação-na-listagem-de-usuários)
2. [Padronizar as mensagens de erros](#2-padronizar-as-mensagens-de-erros)
3. [Adicionar o campo `level` na lista de tecnologias](#3-adicionar-o-campo-level-na-lista-de-tecnologias)
4. [Alterar todos os endpoints para retornar o novo campo `level`](#4-alterar-todos-os-endpoints-para-retornar-o-novo-campo-level)
5. [Deve ser possível buscar a lista de tecnologias de um usuário](#5-deve-ser-possível-buscar-a-lista-de-tecnologias-de-um-usuário)
6. [Implementar testes unitários e de integração](#6-implementar-testes-unitários-e-de-integração)
7. [Etapas opcionais](#7-etapas-opcionais)

### 1. Adicionar paginação e ordenação na listagem de usuários

Adicionar paginação e ordenação na listagem de usuários.
Por padrão, cada página exibe 15 itens e sempre começa da página 0.
Todos os atributos serão passados via query params.

> GET /users

```json
{
  "records": [
    {
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

```shell
GET /users?page_size=30&page=3&sort=-name 
//30 itens por página, mostra a 3 página e ordena de forma decrescente pelo name
```

O código HTTP retornado será [206 Partial Content](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/206), exceto
quando não tiver mais dados na listagem ou os filtros solicitados retorne toda a coleção, nesse caso o retorno
será [200 OK](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/200)

--- 

### 2. Padronizar as mensagens de erros

Todas as mensagens de erros devem seguir a seguinte estrutura:

```json
{
  "error_messages": [
    {
      "code": "",
      "description": ""
    }
  ]
}
```

Essa estrutura é bem parecida com a estrutura de erros da
PagBank https://dev.pagbank.uol.com.br/reference/codigos-de-erro-order

> [!NOTE]
> É interessante adicionar no README do projeto a tabela de códigos de erros.

Exemplos:

```shell
POST /users
{"nick": "Fulano", "birth_date": "2000-01-01T20:20:03Z"}
> {"error_messages":[{"code": "invalid_user", "description": "A user must have a name"}]}
```

```shell
GET /users/999999
> {"error_messages":[{"code": "not_found", "description": "The user with the id ‘999999' doesn't exist"}]}
```

---

### 3. Adicionar o campo `level` e `name` na lista de tecnologias

No endpoint de inclusão e alteração dos usuaŕios, alterar para ser possível informar o nome e o nível de conhecimento em
uma tecnologia.

| Atributo | descrição                                                                                      |
|----------|------------------------------------------------------------------------------------------------|
| name     | obrigatório, string, não é permitido valores nulos e vazios, máximo 32 caracteres, valor único |
| level    | obrigatório, inteiro, não é permitido valores nulos, valores entre 1 .. 100                    |

> POST /users

```json
{
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
```

A combinação `id usuário` + `nome da tecnologia` devem ser únicos. Na tabela stack o `name` pode ser duplicado. Mas não 
deve ser possível inserir um usuário com 2 tecnologias com o mesmo nome. Não faz sentido o usuário ter nível 20 em NodeJS e nível
50 em NodeJS. Mas o nome `NodeJS` pode duplicar dentro da tabela de tecnologias.

---

### 4. Alterar todos os endpoints para retornar o novo campo `level`

Nos endpoints `/users` e `/users/{id}`, alterar para retornar a lista de tecnologias com o novo atributo.

> GET /users/dce61520-7cdf-412e-98a0-50d50bb21493

```json
{
  "id": "dce61520-7cdf-412e-98a0-50d50bb21493",
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
```

---

### 5. Deve ser possível buscar a lista de tecnologias de um usuário

Não é necessário adicionar paginação e ordenação.

> GET /users/dce61520-7cdf-412e-98a0-50d50bb21493/stacks

```json
[
  {
    "name": "Java",
    "level": 50
  },
  {
    "name": "Kotlin",
    "level": 50
  }
]
```

---

### 6. Implementar testes unitários e de integração

Implementar os testes unitários e de integração. Para o teste de integração, utilizar o mesmo banco de dados (Oracle).
Os testes devem atingir pelo menos 85% de cobertura.

- Ao escrever um teste, sempre siga o princípio F.I.R.S.T
    - https://medium.com/@tasdikrahman/f-i-r-s-t-principles-of-testing-1a497acda8d6
    - https://martinfowler.com/bliki/GivenWhenThen.html
    - https://xp123.com/articles/3a-arrange-act-assert/
    - https://dzone.com/articles/7-popular-unit-test-naming
- Em testes unitários não precisa utilizar a annotation `@SpringBootTest`. Como é um teste unitário, ele
  não precisa subir o ambiente do spring para testar.
- Utilizar o [Testcontainer](https://testcontainers.com/) para os testes de integração
- Não utilizar `WebTestClient` para a escrita de testes

Junit introduziu o conceito
de [tags](https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering) para
substituir o uso de categorias. Use este conceito para separar os seus testes de integração e unitários.

---

### 7. Etapas opcionais

#### 7.1. Docker

Execute o seu projeto dentro de um container docker.

Link de referência:

- https://spring.io/guides/topicals/spring-boot-docker/

