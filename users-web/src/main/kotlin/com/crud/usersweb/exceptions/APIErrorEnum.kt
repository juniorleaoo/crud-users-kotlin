package com.crud.usersweb.exceptions

enum class APIErrorEnum(
    val code: String,
    val description: String
) {

    NOT_FOUND("not_found", "Recurso não encontrado"),
    DATE_TIME_INVALID_FORMAT("date_time_invalid_format", "O formato da data está incorreto"),
    INVALID_FORMAT("invalid_format", "Formato inválido de {0}"),

}