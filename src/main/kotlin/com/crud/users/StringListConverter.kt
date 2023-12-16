package com.crud.users

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class StringListConverter : AttributeConverter<List<String>, String> {

    override fun convertToDatabaseColumn(attribute: List<String>?): String = attribute?.joinToString(";") ?: ""

    override fun convertToEntityAttribute(dbData: String?): List<String> = dbData?.split(";") ?: listOf()

}