package com.crud.userswebfluxcoroutine

import java.time.LocalDateTime
import java.util.UUID

data class User(
    var id: UUID?,
    var nick: String?,
    var name: String,
    var birthDate: LocalDateTime,
    var stack: List<String>?,
)