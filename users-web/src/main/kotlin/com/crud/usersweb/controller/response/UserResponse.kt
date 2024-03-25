package com.crud.usersweb.controller.response

import com.crud.usersweb.entity.Stack
import com.crud.usersweb.entity.User
import java.time.LocalDateTime
import java.util.UUID

data class UserResponse(
    val id: UUID?,
    val birthDate: LocalDateTime,
    val nick: String?,
    val name: String,
    val stack: Set<StackResponse>?,
) {
}

data class StackResponse(
    val name: String,
    val level: Int
) {
    constructor(stack: Stack) : this(stack.name, stack.level)
}

fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = id,
        birthDate = birthDate,
        nick = nick,
        name = name,
        stack = stack?.map { StackResponse(it.name, it.level) }?.toSet()
    )
}