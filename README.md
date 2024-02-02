## CRUD de usuário

A ideia principal é implementar o mesmo CRUD com as mesmas especificações em diversas abordagens diferentes,
mas sempre utilizando o `Spring` e `Kotlin`.

- [x] Spring Web
- [x] Spring Web com virtual threads
- [] Spring Web Coroutine
- [] Spring WebFlux sem Coroutine (R2dbcRepository sem suspend fun)
- [x] Spring WebFlux com Coroutine (CoroutineCrudRepository com suspend fun)
    - Não foi implementado com CoroutineCrudRepository, mas foi utilizado o R2dbcRepository. Para mais
      explicações [Clique aqui](#por-que-não-foi-utilizado-coroutinecrudrepository-na-versão-webflux--coroutine).

### Perguntas a ser respondidas

- É possível implementar Spring Web com Coroutine?
- Faz diferença usar Virtual Threads no Spring Web em kotlin?
- Qual abordagem do Spring WebFlux performou melhor?

---

## Especificações

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
  "stack": [
    "Java",
    "NodeJS"
  ]
}
```

---

## Tabela de erros

| Code                     | Description                      |
|--------------------------|----------------------------------|
| not_found                | Recurso não encontrado           |
| date_time_invalid_format | O formato da data está incorreto |
| nome                     |                                  |

---

## Reactive vs Coroutine

Programação reativa é um paradigma de programação que lida com streaming de dados e eventos assíncronos.
No contexto do Spring, Spring WebFlux é um módulo que permite construir aplicações Web reativas no ecossistema Spring.

### Reative

Reactor é uma biblioteca non-blocking usada em Spring WebFlux. Baseada
em [Reactive Streams](https://github.com/reactive-streams/reactive-streams-jvm) e fornece os tipos
Mono e Flux para encapsular os valores.

- Mono -> Parecido com `Optional (Java)`, pode emitir 0..1 valor
- Flux -> Parecido com `List`, pode emitir 0..N valores

### Coroutine

Kotlin coroutine permite escrever código non-blocking, mas em um estilo imperativo e sequencial.
Além disso, coroutine funciona com frameworks non-blocking como o Spring WebFlux, convertendo em `suspend fun` e
fornecendo
tipos Flow para streaming de dados.

## ResponseEntity vs ServerResponse

O `ResponseEntity` faz parte do pacote `Spring MVC` e o `ServerResponse` faz parte do pacote `Spring WebFlux`.
O WebFlux é compatível com os recursos do `Spring MVC`, permitindo utilizar as anotações como `@RestController` que
retornaria um `ResponseEntity`.
O WebFlux sem compatibilidade usa `Router` e `Handler` que retorna um `ServerResponse`.

https://spring.io/guides/gs/reactive-rest-service/#initial

### Por que não foi utilizado CoroutineCrudRepository na versão webflux + coroutine?

