package com.crud.usersweb

import java.time.LocalDate
import java.util.*

data class UserResponse(
    val id: UUID,
    val birthDate: LocalDate,
    val nick: String,
    val name: String,
    val stack: List<String>?,
) {
}