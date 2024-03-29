package com.crud.usersweb.controller

import com.crud.usersweb.AbstractIntegrationTest
import com.crud.usersweb.handlers.ErrorsResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForObject
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.jdbc.JdbcTestUtils
import java.net.URI
import java.time.LocalDateTime
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest : AbstractIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    private var baseUrl: String = "http://localhost"

    @BeforeEach
    fun setUp() {
        baseUrl = "$baseUrl:$port/v1/users"
    }

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

            val createUserResponse = testRestTemplate.postForObject<UserResponse>(baseUrl, userRequest)
            val userResponse = testRestTemplate.getForEntity<UserResponse>("$baseUrl/${createUserResponse?.id}")
            assertNotNull(userResponse)
            assertEquals(HttpStatus.OK, userResponse.statusCode)
            val user = userResponse.body
            assertNotNull(user?.id)
            assertEquals(userRequest.nick, user?.nick)
            assertEquals(userRequest.name, user?.name)
            assertEquals(userRequest.birthDate, user?.birthDate)
            assertEquals(userRequest.stack, user?.stack)
        }

        @Test
        fun `Get user that not exists`() {
            val userResponse = testRestTemplate.getForEntity<UserResponse>("$baseUrl/${UUID.randomUUID()}")
            assertNotNull(userResponse)
            assertEquals(HttpStatus.NOT_FOUND, userResponse.statusCode)
        }

    }

    @Nested
    inner class ListUser {

        @Test
        fun `List users when not have users`() {
            val response = testRestTemplate.getForEntity<List<UserResponse>>(baseUrl)
            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.OK)
            val users = response.body as ArrayList<UserResponse>
            assertTrue(users.isEmpty())
        }

        @Test
        fun `List users when have one user`() {
            val userRequest = CreateUserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.of(2024, 1, 17, 1, 1),
                stack = listOf("NodeJS")
            )

            val userCreated = testRestTemplate.postForObject(baseUrl, userRequest, UserResponse::class.java)

            val response = testRestTemplate.exchange(
                RequestEntity.get(URI(baseUrl)).build(),
                object : ParameterizedTypeReference<List<UserResponse>>() {})

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.OK)

            val users: List<UserResponse>? = response.body
            assertThat(users)
                .isNotNull()
                .hasSize(1)
                .first()
                .usingRecursiveComparison()
                .isEqualTo(
                    UserResponse(
                    id = userCreated.id,
                    name = userRequest.name,
                    nick = userRequest.nick,
                    birthDate = userRequest.birthDate,
                    stack = userRequest.stack,
                )
                )
        }

    }

    @Nested
    inner class CreateUser {

        @Test
        fun `Create User`() {
            val userRequest = CreateUserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.now(),
                stack = listOf("NodeJS")
            )

            val response =
                testRestTemplate.postForEntity(baseUrl, userRequest, UserResponse::class.java)

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.CREATED)
            val user = response.body as UserResponse
            assertNotNull(user)
            assertNotNull(user.id)
            assertEquals(response.headers.location.toString(), "/users/${user.id}")
            assertEquals(user.nick, userRequest.nick)
            assertEquals(user.name, userRequest.name)
            assertEquals(user.birthDate, userRequest.birthDate)
            assertEquals(user.stack, userRequest.stack)
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
            val userRequest = CreateUserRequest(
                name = name,
                nick = "nick",
                birthDate = LocalDateTime.now(),
                stack = listOf("NodeJS")
            )

            val response =
                testRestTemplate.postForEntity(baseUrl, userRequest, ErrorsResponse::class.java)

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.BAD_REQUEST)
            val errors = response.body?.errors

            assertNotNull(errors)
            assertThat(errors)
                .allMatch { it == "O campo nome é obrigatório e deve estar entre 1 e 255" }
        }

        @Test
        fun `Not create user when have empty value on stack`() {
            val userRequest = CreateUserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.now(),
                stack = listOf("", "")
            )

            val response =
                testRestTemplate.postForEntity(baseUrl, userRequest, ErrorsResponse::class.java)

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.BAD_REQUEST)
            val errors = response.body?.errors

            assertNotNull(errors)
            assertThat(errors)
                .allMatch { it == "Os elementos da lista devem estar entre 1 e 32" }
        }

        @Test
        fun `Should create user when not have stack`() {
            val userRequest = CreateUserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.now(),
            )

            val response =
                testRestTemplate.postForEntity(baseUrl, userRequest, UserResponse::class.java)

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.CREATED)
            val user = response.body as UserResponse
            assertNotNull(user)
            assertNotNull(user.id)
            assertEquals(response.headers.location.toString(), "/users/${user.id}")
            assertEquals(user.nick, userRequest.nick)
            assertEquals(user.name, userRequest.name)
            assertEquals(user.birthDate, userRequest.birthDate)
            assertNull(user.stack)
        }

    }

    @Nested
    inner class DeleteUser {

        @Test
        fun `Delete user by id`() {
            val userRequest = CreateUserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.now(),
                stack = listOf("NodeJS")
            )

            val userCreatedResponse = testRestTemplate.postForObject<UserResponse>(baseUrl, userRequest)
            val userDeletedResponse = testRestTemplate.exchange(
                RequestEntity.delete(URI("$baseUrl/${userCreatedResponse?.id}")).build(),
                Nothing::class.java
            )

            assertNotNull(userDeletedResponse)
            assertEquals(HttpStatus.NO_CONTENT, userDeletedResponse.statusCode)
        }

        @Test
        fun `Delete user that not exists`() {
            val userId = UUID.randomUUID()
            val userDeletedResponse = testRestTemplate.exchange(
                RequestEntity.delete(URI("$baseUrl/$userId")).build(),
                Nothing::class.java
            )

            assertNotNull(userDeletedResponse)
            assertEquals(HttpStatus.NOT_FOUND, userDeletedResponse.statusCode)
        }

    }

    @Nested
    inner class UpdateUser {

        @Test
        fun `Update User`() {
            val createUserRequest = CreateUserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.now(),
                stack = listOf("NodeJS")
            )

            val createUserResponse =
                testRestTemplate.postForEntity(baseUrl, createUserRequest, UserResponse::class.java)

            val userCreated = createUserResponse.body as UserResponse
            val updateUserRequest = UpdateUserRequest(
                name = "Name 2",
                nick = "nick 2",
                birthDate = LocalDateTime.of(2023, 12, 1, 1, 1),
                stack = null
            )

            val userId = userCreated.id
            val updateUserResponse = testRestTemplate.exchange(
                RequestEntity<UpdateUserRequest>(
                    updateUserRequest,
                    HttpMethod.PUT,
                    URI("$baseUrl/$userId")
                ), UserResponse::class.java
            )

            assertNotNull(updateUserResponse)
            assertEquals(updateUserResponse.statusCode, HttpStatus.OK)
            val user = updateUserResponse.body as UserResponse
            assertNotNull(user)
            assertEquals(user.id, userId)
            assertEquals(user.nick, updateUserRequest.nick)
            assertEquals(user.name, updateUserRequest.name)
            assertEquals(user.birthDate, updateUserRequest.birthDate)
            assertEquals(user.stack, updateUserRequest.stack)
        }

    }

}