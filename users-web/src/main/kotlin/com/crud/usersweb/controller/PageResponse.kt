package com.crud.usersweb.controller

import org.springframework.data.domain.Page
import java.util.function.Function

data class PageResponse<T>(
    val records: List<T>,
    val page: Int,
    val pageSize: Int,
    val total: Long,
) {}

fun <E, T> Page<E>.toPageResponse(converter: Function<E, T>): PageResponse<T> {
    return PageResponse(
        records = this.map(converter).toList(),
        page = this.number,
        pageSize = this.size,
        total = this.totalElements
    )
}