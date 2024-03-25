package com.crud.usersweb.controller

import com.crud.usersweb.AbstractIntegrationTest
import com.crud.usersweb.controller.request.JobRequest
import com.crud.usersweb.controller.request.UserRequest
import com.crud.usersweb.controller.response.JobResponse
import com.crud.usersweb.controller.response.UserResponse
import com.crud.usersweb.repository.InterviewRepository
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.test.web.client.postForObject
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.LocalDateTime

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InterviewControllerTest(
    @Autowired val testRestTemplate: TestRestTemplate,
    @Autowired val interviewRepository: InterviewRepository
) : AbstractIntegrationTest() {

    @AfterEach
    fun setDown() {
        interviewRepository.deleteAll()
    }

    private fun createUser(): UserResponse? {
        val createUserResponse = testRestTemplate.postForObject<UserResponse>(
            "/users", UserRequest(
                name = "Name",
                nick = "nick",
                birthDate = LocalDateTime.of(2024, 1, 17, 1, 1),
                stack = setOf()
            )
        )
        return createUserResponse
    }

    private fun createJob(): JobResponse? {
        val createJobResponse = testRestTemplate.postForObject<JobResponse>(
            "/jobs", JobRequest(
                name = "Job 1",
                description = "Description 1",
                salary = BigDecimal.ONE,
                requirements = setOf()
            )
        )
        return createJobResponse
    }

    @Test
    fun `Should create interview and return 200`() {
        val createJobResponse = createJob()
        val createUserResponse = createUser()

        val interviewRequest = InterviewRequest(
            userId = createUserResponse?.id!!,
            jobId = createJobResponse?.id!!,
            interviewDate = LocalDateTime.now()
        )

        val interviewResponse = testRestTemplate.postForEntity<Void>("/interviews", interviewRequest)
        interviewResponse.shouldNotBeNull()
        interviewResponse.statusCode shouldBe HttpStatus.OK
        interviewResponse.body.shouldBeNull()
    }

    @Test
    fun `Should not create an interview when the userId does not exist`(){

    }

    @Test
    fun `Should not create an interview when the jobId does not exist`(){

    }

    @Test
    fun `Should not create an interview when the date is invalid`(){

    }

    @Test
    fun `Should not create an interview when the userId is not provided in the payload`(){

    }

    @Test
    fun `Should not create an interview when the jobId is not provided in the payload`(){

    }

    @Test
    fun `Should not create an interview when the date is not provided in the payload`(){

    }

}