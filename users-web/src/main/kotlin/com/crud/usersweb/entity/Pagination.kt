package com.crud.usersweb.entity

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class Pagination(
    private val page: Int,
    private val pageSize: Int,
    private val sortBy: String,
) {

    val sort: Sort
        get() {
            return when {
                sortBy.startsWith("-") -> Sort.by(sortBy.replace("-", "")).descending()
                sortBy.startsWith("+") -> Sort.by(sortBy.replace("+", "")).ascending()
                else -> Sort.by("id").ascending()
            }
        }

    val pageable: Pageable
        get() = PageRequest.of(page, pageSize, sort)

}