package com.crud.users

import java.util.*

fun User.toDTO(): CreateUserResponse {
    return CreateUserResponse(
        id = id,
        birthDate = birthDate,
        nick = nick,
        name = name,
        stack = stack
    )
}

fun CreateUserRequest.toUser(): User {
    return User(
        UUID.randomUUID(),
        nick = nick,
        name = name,
        birthDate = birthDate,
        stack = stack
    )
}