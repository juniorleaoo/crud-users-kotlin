package com.crud.usersweb.service

import com.crud.usersweb.entity.Pagination
import com.crud.usersweb.entity.User
import com.crud.usersweb.repository.StackRepository
import com.crud.usersweb.repository.UserRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.optional.shouldNotBePresent
import io.kotest.matchers.should
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@Tag("unit")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserServiceTest {

    @Autowired
    lateinit var userService: UserService

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var stackRepository: StackRepository

    @Nested
    inner class GetUserById {

        @Test
        fun `Should get user by id and return an optional user`() {
            val id = UUID.randomUUID()
            val user = User(
                id = id,
                nick = "nick",
                name = "name",
                birthDate = LocalDateTime.now(),
                stack = mutableSetOf()
            )

            every { userRepository.findById(id) } returns Optional.of(user)

            val userOptional = userService.findById(id)

            userOptional
                .shouldNotBeNull()
                .shouldBePresent { value ->
                    value.shouldBeEqual(user)
                }
        }

        @Test
        fun `Should get user by id and return an empty optional`() {
            val id = UUID.randomUUID()

            every { userRepository.findById(id) } returns Optional.empty()

            val userOptional = userService.findById(id)

            userOptional.shouldNotBeNull()
                .shouldNotBePresent()
        }
    }

    @Nested
    inner class GetAllUsers {

        @Test
        fun `Should get all users and return a list of users`() {
            val usersMock = listOf(
                User(
                    id = UUID.randomUUID(),
                    nick = "nick",
                    name = "name",
                    birthDate = LocalDateTime.now(),
                    stack = mutableSetOf()
                ),
                User(
                    id = UUID.randomUUID(),
                    nick = "nick",
                    name = "name",
                    birthDate = LocalDateTime.now(),
                    stack = mutableSetOf()
                )
            )

            every { userRepository.findAll() } returns usersMock

            val users = userService.findAll()
            users.shouldNotBeNull()
                .shouldNotBeEmpty()
                .shouldHaveSize(2)
                .shouldBeEqual(usersMock)
        }

        @Test
        fun `Should get all users and return an empty list`() {
            every { userRepository.findAll() } returns emptyList()

            val users = userService.findAll()

            users.shouldNotBeNull()
                .shouldBeEmpty()
        }

        @Test
        fun `Should get a empty page of users`() {
            every { userRepository.count() } returns 0

            val users = userService.findAll(Pagination())

            users.shouldNotBeNull()
                .should {
                    it.totalPages.shouldBeEqual(0)
                    it.totalElements.shouldBeEqual(0)
                }
        }
    }

}