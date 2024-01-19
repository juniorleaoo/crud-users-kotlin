package com.crud.usersweb

import java.time.LocalDateTime
import java.util.*

data class UserResponse(
    val id: UUID,
    val birthDate: LocalDateTime,
    val nick: String?,
    val name: String,
    val stack: List<String>?,
) {
}