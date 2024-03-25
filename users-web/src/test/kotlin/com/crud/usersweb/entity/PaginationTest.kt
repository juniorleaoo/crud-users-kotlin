package com.crud.usersweb.entity

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

@Tag("unit")
class PaginationTest {

    @Test
    fun `Should create a Pagination object with default values`() {
        val pagination = Pagination()

        val sort = pagination.sort
        sort.shouldNotBeNull()
            .shouldBeEqual(Sort.by("id").ascending())

        pagination.pageable.shouldNotBeNull()
            .shouldBeEqual(PageRequest.of(0, 15, sort))
    }

    @Test
    fun `Should create a Pagination object with sort descending name`() {
        val pagination = Pagination(1, 10, "-name")

        val sort = pagination.sort
        sort.shouldNotBeNull()
            .shouldBeEqual(Sort.by("name").descending())

        pagination.pageable.shouldNotBeNull()
            .shouldBeEqual(PageRequest.of(1, 10, sort))
    }

    @Test
    fun `Should create a Pagination object with ascending name`() {
        val pagination = Pagination(1, 10, "+name")

        val sort = pagination.sort
        sort.shouldNotBeNull()
            .shouldBeEqual(Sort.by("name").ascending())

        pagination.pageable.shouldNotBeNull()
            .shouldBeEqual(PageRequest.of(1, 10, sort))
    }

    @Test
    fun `Should not create a Pagination object with empty sort field`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Pagination(0, 10, "")
        }
        exception.shouldHaveMessage("Sort by must not be blank")
    }

    @Test
    fun `Should not create a Sort object with empty sort field ascending`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Pagination(0, 10, "+")
        }
        exception.shouldHaveMessage("Sort need a field")
    }

    @Test
    fun `Should not create a Sort object with empty sort field descending`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Pagination(0, 10, "-")
        }
        exception.shouldHaveMessage("Sort need a field")
    }

    @Test
    fun `Should not create a Pagination object negative page`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Pagination(-1, 10, "+name")
        }
        exception.shouldHaveMessage("Page must be greater than or equal to 0")
    }

    @Test
    fun `Should not create a Pagination object with negative page size`() {
        val exception = shouldThrow<IllegalArgumentException> {
            Pagination(1, -10, "+name")
        }
        exception.shouldHaveMessage("Page size must be greater than 0")
    }

}