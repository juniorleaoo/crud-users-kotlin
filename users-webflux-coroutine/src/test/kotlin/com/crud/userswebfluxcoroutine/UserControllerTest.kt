package com.crud.userswebfluxcoroutine

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.jdbc.JdbcTestUtils
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest : AbstractIntegrationTest() {

    @Autowired
    lateinit var webClient: WebTestClient

    @AfterEach
    fun setDown(@Autowired jdbcTemplate: JdbcTemplate) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users")
    }

    @Nested
    inner class GetUser {

        @Test
        fun `Get user by id`() {
            val userRequest = CreateUserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.of(2024, 1, 17, 1, 1),
                stack = listOf("NodeJS")
            )

            val userCreated = webClient.post().uri("/users")
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().isCreated
                .expectBody(UserResponse::class.java)
                .returnResult()
                .responseBody

            if (userCreated != null) {
                webClient.get().uri("/users/${userCreated.id}")
                    .exchange()
                    .expectStatus().isOk
                    .expectBody(UserResponse::class.java)
                    .isEqualTo(userCreated)
            }

        }

        @Test
        fun `Get user that not exists`() {
        }

    }

    @Nested
    inner class ListUser {

        @Test
        fun `List users when not have users`() {
        }

        @Test
        fun `List users when have one user`() {
        }

    }

    @Nested
    inner class CreateUser {

        @Test
        fun `Create User`() {
        }

        @ParameterizedTest()
        @ValueSource(
            strings = [
                "",
                "abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabc" +
                        "abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabc" +
                        "abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabc" +
                        "abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabc"
            ]
        )
        fun `Not create user when name is invalid value`(name: String) {
        }

        @Test
        fun `Not create user when have empty value on stack`() {
        }

        @Test
        fun `Should create user when not have stack`() {
        }

    }

    @Nested
    inner class DeleteUser {

        @Test
        fun `Delete user by id`() {
        }

        @Test
        fun `Delete user that not exists`() {
        }

    }

    @Nested
    inner class UpdateUser {

        @Test
        fun `Update User`() {
        }

    }

}