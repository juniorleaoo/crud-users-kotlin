package com.crud.usersweb.utils

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity

inline fun <reified T> typeOf() = object : ParameterizedTypeReference<T>() {}

inline fun <reified T : Any> TestRestTemplate.getExchange(requestEntity: RequestEntity<Void?>): ResponseEntity<T> {
    return exchange(
        requestEntity,
        typeOf<T>()
    )
}