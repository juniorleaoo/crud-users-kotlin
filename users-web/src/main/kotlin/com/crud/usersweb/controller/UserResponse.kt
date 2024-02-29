package com.crud.usersweb.controller

import com.crud.usersweb.entity.Stack
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