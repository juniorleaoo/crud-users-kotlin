package com.crud.usersweb.controller.request

import com.crud.usersweb.entity.Stack
import com.crud.usersweb.entity.User
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class UserRequest(
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val birthDate: LocalDateTime,
    @field:Size(max = 32, message = "O campo apelido deve possuir no máximo 32 caracteres")
    val nick: String?,
    @field:Size(min = 1, max = 255, message = "O campo nome é obrigatório e deve estar entre 1 e 255")
    val name: String,
    @field:Valid
    val stack: Set<StackRequest>? = null,
) {
}

data class StackRequest(
    @field:Size(min = 1, max = 32, message = "O campo nome é obrigatório e deve possuir pelo menos 32 caracteres")
    val name: String,
    @field:Min(value = 1, message = "O tamanho minimo para o campo level é 1")
    @field:Max(value = 100, message = "O tamanho máximo para o campo level é 100")
    val level: Int
)

fun UserRequest.toUser(): User {
    val user = User(
        id = null,
        nick = nick,
        name = name,
        birthDate = birthDate,
        stack = mutableSetOf()
    )

    stack?.forEach {
        user.stack?.add(Stack(
            id = null,
            name = it.name,
            level = it.level,
            user = user
        ))
    }

    return user
}