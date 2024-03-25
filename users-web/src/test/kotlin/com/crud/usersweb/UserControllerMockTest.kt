package com.crud.usersweb

import com.crud.usersweb.controller.request.StackRequest
import com.crud.usersweb.controller.request.UserRequest
import com.crud.usersweb.controller.response.UserResponse
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.jdbc.JdbcTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime
import java.util.UUID

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerMockTest(
    @Autowired
    val mockMvc: MockMvc,
    @Autowired
    val mapper: ObjectMapper
) : AbstractIntegrationTest() {

    @AfterEach
    fun setDown(@Autowired jdbcTemplate: JdbcTemplate) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users")
    }

    @Nested
    inner class GetUser {

        @Test
        fun `Get user by id`() {
            val userRequest = UserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.of(2024, 1, 17, 1, 1),
                stack = setOf(StackRequest("NodeJS", 100))
            )

            val response = mockMvc.post("/users") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(userRequest)
            }.andReturn().response.getContentAsString()

            val user = mapper.readValue(response, object : TypeReference<UserResponse>() {})

            mockMvc.get("/users/${user.id}") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$.id", `is`(user.id.toString()))
                    jsonPath("$.name", `is`("Name"))
                    jsonPath("$.nick", `is`("nick"))
                    jsonPath("$.birthDate", `is`("2024-01-17T01:01:00"))
                    jsonPath("$.stack.length()", `is`(1))
                    jsonPath("$.stack.*", containsInAnyOrder("NodeJS"))
                }
            }
        }

        @Test
        fun `List users when not have users`() {
            mockMvc.get("/users/${UUID.randomUUID()}") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isNotFound() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }
        }

    }


}