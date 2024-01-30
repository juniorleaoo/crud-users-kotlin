package com.crud.usersweb.controller

import com.crud.usersweb.entity.User

fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = id,
        birthDate = birthDate,
        nick = nick,
        name = name,
        stack = stack
    )
}