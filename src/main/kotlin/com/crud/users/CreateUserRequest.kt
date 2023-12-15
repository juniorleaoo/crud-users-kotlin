package com.crud.users

import java.time.LocalDate

data class CreateUserRequest(
    val birthDate: LocalDate,
    val nick: String,
    val name: String,
    val stack: List<String>?,
) {
    init {
        require(name.length in 0..100)
        stack?.forEach {
            require(it.length in 0..32)
        }
    }
}