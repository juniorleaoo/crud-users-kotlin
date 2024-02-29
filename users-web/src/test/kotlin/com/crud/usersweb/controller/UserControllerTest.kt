package com.crud.usersweb.controller

import com.crud.usersweb.AbstractIntegrationTest
import com.crud.usersweb.exceptions.APIErrorEnum.DATE_TIME_INVALID_FORMAT
import com.crud.usersweb.exceptions.APIErrorEnum.NOT_FOUND
import com.crud.usersweb.exceptions.handlers.ErrorMessage
import com.crud.usersweb.exceptions.handlers.ErrorsResponse
import com.crud.usersweb.repository.UserRepository
import com.crud.usersweb.utils.PopulateUsers
import com.crud.usersweb.utils.typeOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.*
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import java.net.URI
import java.time.LocalDateTime
import java.util.*

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest(
    @Autowired val testRestTemplate: TestRestTemplate,
    @Autowired val userRepository: UserRepository
) : AbstractIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0
    private var baseUrl: String = "http://localhost"
    private val populateUsers = PopulateUsers(userRepository)

    @BeforeEach
    fun setUp() {
        baseUrl = "$baseUrl:$port/v1/users"
    }

    @AfterEach
    fun setDown() {
        userRepository.deleteAll()
    }

    @Nested
    inner class GetUser {

        @Test
        fun `Should get user by id`() {
            val stackRequest = StackRequest(
                name = "NodeJS",
                level = 100
            )
            val userRequest = UserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.of(2024, 1, 17, 1, 1),
                stack = setOf(stackRequest)
            )

            val createUserResponse = testRestTemplate.postForObject<UserResponse>(baseUrl, userRequest)
            val userResponse = testRestTemplate.getForEntity<UserResponse>("$baseUrl/${createUserResponse?.id}")
            assertNotNull(userResponse)
            assertEquals(HttpStatus.OK, userResponse.statusCode)
            assertNotNull(userResponse.body)
            val user = userResponse.body as UserResponse
            assertNotNull(user.id)
            assertEquals(userRequest.nick, user.nick)
            assertEquals(userRequest.name, user.name)
            assertEquals(userRequest.birthDate, user.birthDate)
            assertThat(user.stack)
                .isNotNull()
                .hasSize(1)
                .first()
                .usingDefaultComparator()
                .isEqualTo(StackResponse(stackRequest.name, stackRequest.level))
        }

        @Test
        fun `Should return not found when user id not exists`() {
            val userResponse = testRestTemplate.getForEntity<ErrorsResponse>("$baseUrl/${UUID.randomUUID()}")
            assertNotNull(userResponse)
            assertEquals(HttpStatus.NOT_FOUND, userResponse.statusCode)
            val errorsResponse = userResponse.body as ErrorsResponse
            assertThat(errorsResponse.errorMessages)
                .isNotNull
                .hasSizeGreaterThanOrEqualTo(1)
                .allMatch { it.code == NOT_FOUND.code && it.description == NOT_FOUND.description }
        }

    }

    @Nested
    inner class ListUser {

        @Test
        fun `Should return an empty list users when not have users`() {
            val response = testRestTemplate.getForEntity<PageResponse<UserResponse>>(baseUrl)
            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.OK)
            val usersPageResponse = response.body as PageResponse<UserResponse>
            assertEquals(0, usersPageResponse.page)
            assertEquals(15, usersPageResponse.pageSize)
            assertEquals(0, usersPageResponse.total)
            assertThat(usersPageResponse.records)
                .isNotNull
                .hasSize(0)
        }

        @Test
        fun `Should list users when have one user`() {
            val stackRequest = StackRequest(
                name = "NodeJS",
                level = 99
            )
            val userRequest = UserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.of(2024, 1, 17, 1, 1),
                stack = setOf(stackRequest)
            )

            val userCreated = testRestTemplate.postForObject(baseUrl, userRequest, UserResponse::class.java)

            val response = testRestTemplate.exchange(
                RequestEntity.get(URI(baseUrl)).build(),
                typeOf<PageResponse<UserResponse>>())

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.OK)

            val usersPageResponse: PageResponse<UserResponse> = response.body!!
            assertEquals(0, usersPageResponse.page)
            assertEquals(15, usersPageResponse.pageSize)
            assertEquals(1, usersPageResponse.total)

            assertThat(usersPageResponse.records)
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
                        stack = setOf(StackResponse(stackRequest.name, stackRequest.level))
                    )
                )
        }

        @Test
        fun `Should list users on page 2 with 10 itens per page`() {
            val amountUsers = 50L
            populateUsers.createUser(amountUsers)

            val response = testRestTemplate.exchange(
                RequestEntity.get(URI("$baseUrl?page=2&page_size=10")).build(),
                typeOf<PageResponse<UserResponse>>())

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.PARTIAL_CONTENT)

            val usersPageResponse: PageResponse<UserResponse> = response.body!!
            assertEquals(2, usersPageResponse.page)
            assertEquals(10, usersPageResponse.pageSize)
            assertEquals(amountUsers, usersPageResponse.total)
            assertThat(usersPageResponse.records)
                .isNotNull()
                .hasSize(10)
        }

        @Test
        fun `Should list users order by name`() {
            val amountUsers = 50L
            populateUsers.createUser(amountUsers)//insere 50 usuários

            val response = testRestTemplate.exchange(
                RequestEntity.get(URI("$baseUrl?page=1&page_size=10&sort=-name")).build(),
                typeOf<PageResponse<UserResponse>>())

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.PARTIAL_CONTENT)
            val usersPageResponse: PageResponse<UserResponse> = response.body!!
            assertEquals(1, usersPageResponse.page)
            assertEquals(10, usersPageResponse.pageSize)
            assertEquals(amountUsers, usersPageResponse.total)
            assertThat(usersPageResponse.records)
                .isNotNull()
                .hasSize(10)
                .isSortedAccordingTo(compareBy(UserResponse::name).reversed())
        }

    }

    @Nested
    inner class CreateUser {

        @Test
        fun `Should create a complete user with success`() {
            val stackRequest = StackRequest(
                name = "NodeJS",
                level = 100
            )
            val userRequest = UserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.now(),
                stack = setOf(stackRequest)
            )

            val response =
                testRestTemplate.postForEntity<UserResponse>(baseUrl, userRequest)

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.CREATED)
            val user = response.body as UserResponse
            assertNotNull(user)
            assertNotNull(user.id)
            assertEquals(response.headers.location.toString(), "/users/${user.id}")
            assertEquals(userRequest.nick, user.nick)
            assertEquals(userRequest.name, user.name)
            assertEquals(userRequest.birthDate, user.birthDate)
            assertThat(user.stack)
                .isNotNull()
                .hasSize(1)
                .first()
                .usingDefaultComparator()
                .isEqualTo(StackResponse(stackRequest.name, stackRequest.level))
        }

        @Test
        fun `Should create user when nick has 30 characters`() {
            val stackRequest = StackRequest(
                name = "NodeJS",
                level = 100
            )
            val userRequest = UserRequest(
                name = "Name",
                nick = "n".repeat(30),
                birthDate = LocalDateTime.now(),
                stack = setOf(stackRequest)
            )

            val response =
                testRestTemplate.postForEntity<UserResponse>(baseUrl, userRequest)

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.CREATED)
            val user = response.body as UserResponse
            assertNotNull(user)
            assertNotNull(user.id)
            assertEquals(response.headers.location.toString(), "/users/${user.id}")
            assertEquals(userRequest.nick, user.nick)
            assertEquals(userRequest.name, user.name)
            assertEquals(userRequest.birthDate, user.birthDate)
            assertThat(user.stack)
                .isNotNull()
                .hasSize(1)
                .first()
                .usingDefaultComparator()
                .isEqualTo(StackResponse(stackRequest.name, stackRequest.level))
        }

        @Test
        fun `Should not create user when nick has more 30 characters`() {
            val userRequest = UserRequest(
                name = "Name",
                nick = "nick".repeat(30),
                birthDate = LocalDateTime.now(),
                stack = setOf(
                    StackRequest(
                        name = "NodeJS",
                        level = 100
                    )
                )
            )

            val response =
                testRestTemplate.postForEntity(baseUrl, userRequest, ErrorsResponse::class.java)

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.BAD_REQUEST)
            val errors = response.body?.errorMessages

            assertNotNull(errors)
            assertThat(errors)
                .isNotNull
                .hasSizeGreaterThanOrEqualTo(1)
                .allMatch {
                    it.code == "size" &&
                            it.description == "O campo apelido deve possuir no máximo 32 caracteres"
                }
        }

        @Test
        fun `Should create user when not have nick`() {
            val userRequest = UserRequest(
                name = "Name",
                nick = null,
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
            assertNull(user.nick)
            assertEquals(user.name, userRequest.name)
            assertEquals(user.birthDate, userRequest.birthDate)
            assertThat(user.stack)
                .isNotNull()
                .isEmpty()
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
        fun `Should not create user when name is invalid value`(name: String) {
            val userRequest = UserRequest(
                name = name,
                nick = "nick",
                birthDate = LocalDateTime.now(),
                stack = setOf(
                    StackRequest(
                        name = "NodeJS",
                        level = 100
                    )
                )
            )

            val response =
                testRestTemplate.postForEntity<ErrorsResponse>(baseUrl, userRequest)

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.BAD_REQUEST)
            val errors = response.body?.errorMessages

            assertNotNull(errors)
            assertThat(errors)
                .isNotNull
                .hasSizeGreaterThanOrEqualTo(1)
                .allMatch {
                    it.code == "size" &&
                    it.description == "O campo nome é obrigatório e deve estar entre 1 e 255"
                }
        }

        @Test
        fun `Should not create user when stack is empty`() {
            val response = testRestTemplate.exchange<ErrorsResponse>(
                RequestEntity.post(baseUrl).body(mapOf(
                    "name" to "Fulano",
                    "nick" to "Nick",
                    "birth_date" to "2024-10-01T01:10:01",
                    "stack" to listOf("", "")
                ))
            )

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.BAD_REQUEST)
            val errors = response.body?.errorMessages

            assertNotNull(errors)
            assertThat(errors)
                .isNotNull
                .hasSizeGreaterThanOrEqualTo(1)
                .allMatch { it.description == "Formato inválido de stack" }
        }

        @Test
        fun `Should create user stack is null`() {
            val userRequest = UserRequest(
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
            assertThat(user.stack)
                .isNotNull()
                .isEmpty()
        }

        @Test
        fun `Should not create user when date is invalid`() {
            val response = testRestTemplate.exchange<ErrorsResponse>(
                RequestEntity.post(baseUrl).body(mapOf(
                    "name" to "Fulano",
                    "birth_date" to "2024-10-0101:10:01",
                ))
            )

            assertNotNull(response)
            assertEquals(response.statusCode, HttpStatus.BAD_REQUEST)
            val errors = response.body?.errorMessages

            assertNotNull(errors)
            assertThat(errors)
                .isNotNull
                .hasSizeGreaterThanOrEqualTo(1)
                .first()
                .isEqualTo(ErrorMessage(DATE_TIME_INVALID_FORMAT.code, DATE_TIME_INVALID_FORMAT.description))
        }

    }

    @Nested
    inner class DeleteUser {

        @Test
        fun `Should delete user by id with success`() {
            val userRequest = UserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.now(),
                stack = setOf(
                    StackRequest(
                        name = "NodeJS",
                        level = 100
                    )
                )
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
        fun `Should return not found when delete user id not exists`() {
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
        fun `Should change name, nick and clear stack on update user`() {
            val userRequest = UserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.now(),
                stack = setOf(
                    StackRequest(
                        name = "NodeJS",
                        level = 100
                    )
                )
            )

            val createUserResponse =
                testRestTemplate.postForEntity(baseUrl, userRequest, UserResponse::class.java)

            val userCreated = createUserResponse.body as UserResponse
            val updateUserRequest = UserRequest(
                name = "Name 2",
                nick = "nick 2",
                birthDate = LocalDateTime.of(2023, 12, 1, 1, 1),
                stack = null
            )

            val userId = userCreated.id
            val userUpdatedResponse = testRestTemplate.exchange(
                RequestEntity<UserRequest>(
                    updateUserRequest,
                    HttpMethod.PUT,
                    URI("$baseUrl/$userId")
                ), UserResponse::class.java
            )

            assertNotNull(userUpdatedResponse)
            assertEquals(userUpdatedResponse.statusCode, HttpStatus.OK)
            val userUpdated = userUpdatedResponse.body as UserResponse
            assertNotNull(userUpdated)
            assertEquals(userUpdated.id, userId)
            assertEquals(userUpdated.nick, updateUserRequest.nick)
            assertEquals(userUpdated.name, updateUserRequest.name)
            assertEquals(userUpdated.birthDate, updateUserRequest.birthDate)
            assertThat(userUpdated.stack)
                .isNotNull()
                .isEmpty()
        }

    }

    @Nested
    inner class GetStacks {

        @Test
        fun `Should return an empty list of stacks`() {
            val userRequest = UserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.of(2024, 1, 17, 1, 1),
                stack = setOf()
            )

            val createUserResponse = testRestTemplate.postForObject<UserResponse>(baseUrl, userRequest)
            val stacksResponse = testRestTemplate.exchange(
                RequestEntity.get(URI("$baseUrl/${createUserResponse?.id}/stacks")).build(),
                typeOf<List<StackResponse>>())

            assertNotNull(stacksResponse)
            assertEquals(HttpStatus.OK, stacksResponse.statusCode)
            assertThat(stacksResponse.body)
                .isNotNull()
                .hasSize(0)
        }

        @Test
        fun `Should list of stacks`() {
            val userRequest = UserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.of(2024, 1, 17, 1, 1),
                stack = setOf(StackRequest("NodeJS", 100), StackRequest("Java", 100))
            )

            val createUserResponse = testRestTemplate.postForObject<UserResponse>(baseUrl, userRequest)
            val stacksResponse = testRestTemplate.exchange(
                RequestEntity.get(URI("$baseUrl/${createUserResponse?.id}/stacks")).build(),
                typeOf<List<StackResponse>>())

            assertNotNull(stacksResponse)
            assertEquals(HttpStatus.OK, stacksResponse.statusCode)
            assertThat(stacksResponse.body as List<StackResponse>)
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder(StackResponse("NodeJS", 100), StackResponse("Java", 100))
        }

        @Test
        fun `Should list same stacks of user after update user`() {
            val userRequest = UserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.now(),
                stack = setOf(StackRequest("NodeJS", 100))
            )

            val createUserResponse = testRestTemplate.postForEntity<UserResponse>(baseUrl, userRequest)
            val userCreated = createUserResponse.body as UserResponse

            val stacksResponse = testRestTemplate.exchange<List<StackResponse>>(
                RequestEntity.get(URI("$baseUrl/${userCreated.id}/stacks")).build()
            )

            assertNotNull(stacksResponse)
            assertEquals(HttpStatus.OK, stacksResponse.statusCode)
            assertThat(stacksResponse.body)
                .isNotNull
                .hasSize(1)
                .containsExactlyInAnyOrder(StackResponse("NodeJS", 100))

            val updateUserRequest = UserRequest(
                name = "Name 2",
                nick = "nick 2",
                birthDate = userCreated.birthDate,
                stack = userCreated.stack?.map { StackRequest(it.name, it.level) }?.toSet()
            )

            testRestTemplate.exchange(
                RequestEntity<UserRequest>(
                    updateUserRequest,
                    HttpMethod.PUT,
                    URI("$baseUrl/${userCreated.id}")
                ), UserResponse::class.java
            )

            val stacksResponseAfterUpdate = testRestTemplate.exchange(
                RequestEntity.get(URI("$baseUrl/${userCreated.id}/stacks")).build(),
                typeOf<List<StackResponse>>())
            assertNotNull(stacksResponseAfterUpdate)
            assertEquals(HttpStatus.OK, stacksResponseAfterUpdate.statusCode)
            assertThat(stacksResponseAfterUpdate.body)
                .isNotNull
                .hasSize(1)
                .containsExactlyInAnyOrder(StackResponse("NodeJS", 100))
        }

        @Test
        fun `Should return not found when user id not exists`() {
            val userResponse = testRestTemplate.getForEntity<ErrorsResponse>("$baseUrl/${UUID.randomUUID()}/stacks")
            assertNotNull(userResponse)
            assertEquals(HttpStatus.NOT_FOUND, userResponse.statusCode)
            val errorsResponse = userResponse.body as ErrorsResponse
            assertThat(errorsResponse.errorMessages)
                .isNotNull
                .hasSizeGreaterThanOrEqualTo(1)
                .allMatch { it.code == NOT_FOUND.code && it.description == NOT_FOUND.description }
        }

    }

}