package com.crud.users

import java.time.LocalDate
import java.util.*

data class CreateUserResponse(
    val id: UUID,
    val birthDate: LocalDate,
    val nick: String,
    val name: String,
    val stack: List<String>?,
) {

    constructor(user: User): this(
        user.id,
        user.birthDate,
        user.nick,
        user.name,
        user.stack
    )

}