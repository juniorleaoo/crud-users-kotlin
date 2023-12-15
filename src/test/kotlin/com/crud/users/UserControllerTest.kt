package com.crud.users

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.jdbc.JdbcTestUtils
import java.time.LocalDate

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

    @Test
    fun `List users when not have users`() {
        val response = testRestTemplate.getForEntity<MutableList<User>>(baseUrl)
        assertNotNull(response)
        assertEquals(response.statusCode, HttpStatus.OK)
        val users = response.body as ArrayList<User>
        assertTrue(users.isEmpty())
    }

    @Test
    fun `List users when have one user`() {
        val userRequest = CreateUserRequest(
            name = "Name",
            nick = "nick",
            birthDate = LocalDate.now(),
            stack = listOf("NodeJS")
        )

        testRestTemplate.postForObject(baseUrl, userRequest, CreateUserRequest::class.java)

        val response = testRestTemplate.getForEntity<MutableList<User>>(baseUrl, ArrayList<User>().javaClass)
        assertNotNull(response)
        assertEquals(response.statusCode, HttpStatus.OK)
        val users = response.body as ArrayList<User>
        assertEquals(users.size, 1)
        assertEquals(users[0].nick, userRequest.nick)
        assertEquals(users[0].name, userRequest.name)
        assertEquals(users[0].birthDate, userRequest.birthDate)
        assertEquals(users[0].stack, userRequest.stack)
    }

    @Test
    fun `Create User`() {
        val userRequest = CreateUserRequest(
            name = "Name",
            nick = "nick",
            birthDate = LocalDate.now(),
            stack = listOf("NodeJS")
        )

        val response =
            testRestTemplate.postForEntity(baseUrl, userRequest, CreateUserResponse::class.java)

        assertNotNull(response)
        assertEquals(response.statusCode, HttpStatus.CREATED)
        val user = response.body as CreateUserResponse
        assertNotNull(user)
        assertNotNull(user.id)
        assertEquals(response.headers.location, "$baseUrl/${user.id}")
        assertEquals(user.nick, userRequest.nick)
        assertEquals(user.name, userRequest.name)
        assertEquals(user.birthDate, userRequest.birthDate)
        assertEquals(user.stack, userRequest.stack)
    }

    @Test
    fun `Delete user by id`(){
        val userRequest = CreateUserRequest(
            name = "Name",
            nick = "nick",
            birthDate = LocalDate.now(),
            stack = listOf("NodeJS")
        )

        val userCreatedResponse = testRestTemplate.postForEntity(baseUrl, userRequest, CreateUserResponse::class.java)
        val userCreated = userCreatedResponse.body as CreateUserResponse

        val userDeletedResponse = testRestTemplate.delete("$baseUrl/$userCreated.id")
        assertNotNull(userDeletedResponse)
    }

}