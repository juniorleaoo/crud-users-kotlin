package com.crud.userswebfluxcoroutine

import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateUserRequest(
    val birthDate: LocalDateTime,
    @field:Size(min = 1, max = 32, message = "O campo apelido deve estar entre 1 e 32")
    val nick: String?,
    @field:Size(min = 1, max = 255, message = "O campo nome é obrigatório e deve estar entre 1 e 255")
    val name: String,
    @field:SizeElementsOfList
    val stack: List<String>? = null,
) {
}

fun CreateUserRequest.toUser(): User {
    return User(
        id = null,
        nick = nick,
        name = name,
        birthDate = birthDate,
        stack = stack
    )
}

data class UpdateUserRequest(
    var birthDate: LocalDateTime?,
    @field:Size(min= 1, max = 32, message = "O campo apelido deve estar entre 1 e 32")
    var nick: String?,
    @field:Size(min = 1, max = 255, message = "O campo nome é obrigatório e deve estar entre 1 e 255")
    var name: String?,
    @field:SizeElementsOfList
    var stack: List<String>?,
) {}