package com.crud.usersweb.entity

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class Pagination(
    private val page: Int = 0,
    private val pageSize: Int = 15,
    private val sortBy: String = "id",
) {

    init {
        require(page >= 0) { "Page must be greater than or equal to 0" }
        require(pageSize > 0) { "Page size must be greater than 0" }
        require(sortBy.isNotBlank()) { "Sort by must not be blank" }
        require(sortBy.replace("-", "").replace("+", "").isNotBlank()) { "Sort need a field" }
    }

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