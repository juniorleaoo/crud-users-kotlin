package com.crud.users

import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateUserRequest(
    val birthDate: LocalDate,
    @field:Size(min = 1, max = 32, message = "O campo apelido é obrigatório e deve estar entre 1 e 32")
    val nick: String,
    @field:Size(min = 1, max = 255, message = "O campo nome é obrigatório e deve estar entre 1 e 255")
    val name: String,
    val stack: List<String>?,
) {
}