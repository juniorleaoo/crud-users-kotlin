package com.crud.usersweb

import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateUserRequest(
    val birthDate: LocalDate,
    @field:Size(max = 32, message = "O campo apelido deve estar entre 1 e 32")
    val nick: String,
    @field:Size(min = 1, max = 255, message = "O campo nome é obrigatório e deve estar entre 1 e 255")
    val name: String,
    val stack: List<String>?,
) {
}

fun CreateUserRequest.toUser(): User {
    return User(
        nick = nick,
        name = name,
        birthDate = birthDate,
        stack = stack
    )
}

data class UpdateUserRequest(
    var birthDate: LocalDate?,
    var nick: String?,
    var name: String?,
    var stack: List<String>?,
) {}