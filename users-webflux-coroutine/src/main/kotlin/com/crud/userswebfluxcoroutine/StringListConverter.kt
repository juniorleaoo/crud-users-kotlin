package com.crud.userswebfluxcoroutine

object StringListConverter {

    fun convertToDatabaseColumn(attribute: List<String>?): String = attribute?.joinToString(";") ?: ""

    fun convertToEntityAttribute(dbData: String?): List<String> =
        if (dbData?.isNotBlank() == true) dbData.split(";") else listOf()

}