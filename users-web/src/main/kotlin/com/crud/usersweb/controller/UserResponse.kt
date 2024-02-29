package com.crud.usersweb.controller

import java.time.LocalDateTime
import java.util.*

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
)