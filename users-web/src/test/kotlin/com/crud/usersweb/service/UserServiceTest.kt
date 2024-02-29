package com.crud.usersweb.service

import com.crud.usersweb.entity.User
import com.crud.usersweb.repository.StackRepository
import com.crud.usersweb.repository.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.*

@Tag("unit")
@SpringBootTest
class UserServiceTest {

    @Autowired
    lateinit var userService: UserService

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var stackRepository: StackRepository

    @Nested
    inner class GetUser {

        @Test
        fun `Should return user by id`() {
            val id = UUID.randomUUID()

            every { userRepository.findById(id) } returns Optional.of(User(
                id = id,
                nick = "Nick",
                name = "Name",
                birthDate = LocalDateTime.now(),
                stack = mutableSetOf()
            ))

            val userOptional = userService.findById(id)

            assertNotNull(userOptional)
            assertThat(userOptional)
                .isNotEmpty
                .hasValue(User())
        }

    }

}