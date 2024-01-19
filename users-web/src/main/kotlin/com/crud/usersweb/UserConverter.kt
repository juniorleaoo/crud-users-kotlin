package com.crud.usersweb

fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = id,
        birthDate = birthDate,
        nick = nick,
        name = name,
        stack = stack
    )
}