package com.crud.usersweb.controller

import com.crud.usersweb.AbstractIntegrationTest
import com.crud.usersweb.controller.request.JobRequest
import com.crud.usersweb.controller.request.LevelRequest
import com.crud.usersweb.controller.request.RequirementRequest
import com.crud.usersweb.controller.response.JobResponse
import com.crud.usersweb.controller.response.LevelResponse
import com.crud.usersweb.controller.response.PageResponse
import com.crud.usersweb.controller.response.RequirementResponse
import com.crud.usersweb.exceptions.APIErrorEnum
import com.crud.usersweb.exceptions.handlers.ErrorMessage
import com.crud.usersweb.exceptions.handlers.ErrorsResponse
import com.crud.usersweb.repository.JobRepository
import com.crud.usersweb.utils.PopulateJobs
import com.crud.usersweb.utils.typeOf
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.test.web.client.postForObject
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import java.net.URI
import java.util.UUID

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobControllerTest(
    @Autowired val testRestTemplate: TestRestTemplate,
    @Autowired val jobRepository: JobRepository
) : AbstractIntegrationTest() {

    private val populateJobs = PopulateJobs(jobRepository)

    @AfterEach
    fun setDown() {
        jobRepository.deleteAll()
    }

    @Nested
    inner class GetJob {

        @Test
        fun `Should get job by id and returns 200`() {
            val requirementRequest = RequirementRequest(
                stack = "Java",
                level = LevelRequest(min = 1, max = 2)
            )
            val jobRequest = JobRequest(
                name = "Software Engineer",
                description = "Develop software",
                salary = 1,
                requirements = setOf(requirementRequest)
            )
            val createJobResponse = testRestTemplate.postForObject<JobResponse>("/jobs", jobRequest)
            val jobResponse = testRestTemplate.getForEntity<JobResponse>("/jobs/${createJobResponse?.id}")

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.OK
            jobResponse.body.shouldNotBeNull()
                .should {
                    it.name shouldBe jobRequest.name
                    it.description shouldBe jobRequest.description
                    it.salary shouldBe jobRequest.salary
                    it.requirements.shouldHaveSize(1)
                        .shouldHaveSingleElement(
                            RequirementResponse(
                                stack = requirementRequest.stack,
                                level = LevelResponse(
                                    min = requirementRequest.level?.min,
                                    max = requirementRequest.level?.max
                                )
                            )
                        )
                }
        }

        @Test
        fun `Should get job by id that not exists and returns 404`() {
            val jobResponse = testRestTemplate.getForEntity<ErrorsResponse>("/jobs/${UUID.randomUUID()}")

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.NOT_FOUND
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .shouldContainExactly(
                    ErrorMessage(
                        APIErrorEnum.NOT_FOUND.code,
                        APIErrorEnum.NOT_FOUND.description
                    )
                )
        }

    }

    @Nested
    inner class ListJob {

        @Test
        fun `Should return an empty list of jobs when there is no job`() {
            val jobResponse = testRestTemplate.getForEntity<PageResponse<JobResponse>>("/jobs")

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.OK
            val jobsPage = jobResponse.body.shouldNotBeNull()
            jobsPage.page shouldBe 0
            jobsPage.total shouldBe 0
            jobsPage.pageSize shouldBe 15
            jobsPage.records.shouldNotBeNull()
                .shouldHaveSize(0)
        }

        @Test
        fun `Should list jobs when have one job`() {
            val requirementRequest = RequirementRequest(
                stack = "Java",
                level = LevelRequest(min = 1, max = 2)
            )
            val jobRequest = JobRequest(
                name = "Software Engineer",
                description = "Develop software",
                salary = 1,
                requirements = setOf(requirementRequest)
            )
            val jobCreated = testRestTemplate.postForObject<JobResponse>("/jobs", jobRequest)
            jobCreated.shouldNotBeNull()

            val jobResponse = testRestTemplate.exchange(
                RequestEntity.get(URI("/jobs")).build(),
                typeOf<PageResponse<JobResponse>>()
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.OK
            val jobsPage = jobResponse.body.shouldNotBeNull()
            jobsPage.page shouldBe 0
            jobsPage.total shouldBe 1
            jobsPage.pageSize shouldBe 15
            jobsPage.records.shouldNotBeNull()
                .shouldHaveSize(1)
                .shouldHaveSingleElement(
                    JobResponse(
                        id = jobCreated.id,
                        name = jobRequest.name,
                        description = jobRequest.description,
                        salary = jobRequest.salary,
                        requirements = setOf(
                            RequirementResponse(
                                stack = requirementRequest.stack,
                                level = LevelResponse(
                                    min = requirementRequest.level?.min,
                                    max = requirementRequest.level?.max
                                )
                            )
                        )
                    )
                )

        }

        @Test
        fun `Should list jobs on page 2 with 10 items per page`() {
            val amountJobs = 50L
            populateJobs.createJob(amountJobs)

            val jobResponse = testRestTemplate.exchange(
                RequestEntity.get(URI("/jobs?page=2&page_size=10")).build(),
                typeOf<PageResponse<JobResponse>>()
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.PARTIAL_CONTENT
            val jobsPage = jobResponse.body.shouldNotBeNull()
            jobsPage.page shouldBe 2
            jobsPage.total shouldBe amountJobs
            jobsPage.pageSize shouldBe 10
            jobsPage.records.shouldNotBeNull()
                .shouldHaveSize(10)
        }

        @Test
        fun `Should list jobs order by name`() {
            val amountJobs = 50L
            populateJobs.createJob(amountJobs)

            val jobResponse = testRestTemplate.exchange(
                RequestEntity.get(URI("/jobs?page=1&page_size=10&sort=-name")).build(),
                typeOf<PageResponse<JobResponse>>()
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.PARTIAL_CONTENT
            val jobsPage = jobResponse.body.shouldNotBeNull()
            jobsPage.page shouldBe 1
            jobsPage.total shouldBe amountJobs
            jobsPage.pageSize shouldBe 10
            jobsPage.records.shouldNotBeNull()
                .shouldHaveSize(10)
                .sortedByDescending { it.name }
        }

    }

    @Nested
    inner class CreateJob {

        @Test
        fun `Should create a complete job and returns 201`() {
            val requirementRequest = RequirementRequest(
                stack = "Kotlin",
                level = LevelRequest(min = 1, max = 2)
            )
            val jobRequest = JobRequest(
                name = "Name",
                description = "description",
                salary = 1,
                requirements = setOf(requirementRequest)
            )
            val jobResponse = testRestTemplate.postForEntity<JobResponse>("/jobs", jobRequest)

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.CREATED
            val job = jobResponse.body.shouldNotBeNull()
            job.name shouldBe jobRequest.name
            job.description shouldBe jobRequest.description
            job.salary shouldBe jobRequest.salary
            job.requirements.shouldHaveSize(1).shouldHaveSingleElement(
                RequirementResponse(
                    stack = requirementRequest.stack,
                    level = LevelResponse(
                        min = requirementRequest.level?.min,
                        max = requirementRequest.level?.max
                    )
                )
            )
        }

        @Test
        fun `Should create a job with name has 500 characters and returns 201`() {
            val jobRequest = JobRequest(
                name = "a".repeat(500),
                description = "description",
                salary = 1,
                requirements = setOf(
                    RequirementRequest(
                    stack = "Kotlin",
                    level = LevelRequest(min = 1, max = 2)
                )
                )
            )
            val jobResponse = testRestTemplate.postForEntity<JobResponse>("/jobs", jobRequest)

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.CREATED
            val job = jobResponse.body.shouldNotBeNull()
            job.name shouldBe jobRequest.name
            job.description shouldBe jobRequest.description
            job.salary shouldBe jobRequest.salary
            job.requirements.shouldNotBeNull().shouldHaveSize(1)
        }

        @Test
        fun `Should create a job with name has more 500 characters and returns 201`() {
            val jobRequest = JobRequest(
                name = "a".repeat(501),
                description = "description",
                salary = 1,
                requirements = setOf<RequirementRequest>(
                    RequirementRequest(
                        stack = "Kotlin",
                        level = LevelRequest(min = 1, max = 2)
                    )
                )
            )
            val jobResponse = testRestTemplate.postForEntity<ErrorsResponse>("/jobs", jobRequest)

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.BAD_REQUEST
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .first()
                .should {
                    it.code shouldBe "size"
                    it.description shouldBe "O campo nome é obrigatório e deve estar entre 1 e 500"
                }
        }

        @Test
        fun `Should not create a job when name is empty`() {
            val jobRequest = JobRequest(
                name = "",
                description = "description",
                salary = 1,
                requirements = setOf(RequirementRequest(
                    stack = "Kotlin",
                    level = LevelRequest(min = 1, max = 2)
                ))
            )
            val jobResponse = testRestTemplate.postForEntity<ErrorsResponse>("/jobs", jobRequest)

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.BAD_REQUEST
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .first()
                .should {
                    it.code shouldBe "size"
                    it.description shouldBe "O campo nome é obrigatório e deve estar entre 1 e 500"
                }
        }

        @Test
        fun `Should not create a job when name is null`() {
            val jobResponse = testRestTemplate.exchange<ErrorsResponse>(
                RequestEntity.post(URI("/jobs")).body(
                    mapOf(
                        "name" to null,
                        "description" to "description",
                        "salary" to 1,
                        "requirements" to setOf(RequirementRequest(
                            stack = "Kotlin",
                            level = LevelRequest(min = 1, max = 2)
                        ))
                    )
                )
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.BAD_REQUEST
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .first()
                .should {
                    it.code shouldBe "size"
                    it.description shouldBe "O campo name é obrigatório"
                }
        }

        @Test
        fun `Should not create a job when salary is null`() {
            val jobResponse = testRestTemplate.exchange<ErrorsResponse>(
                RequestEntity.post(URI("/jobs")).body(
                    mapOf(
                        "name" to "name",
                        "description" to "description",
                        "salary" to null,
                        "requirements" to setOf(RequirementRequest(
                            stack = "Kotlin",
                            level = LevelRequest(min = 1, max = 2))
                        )
                    )
                )
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.BAD_REQUEST
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .first()
                .should {
                    it.code shouldBe "digits"
                    it.description shouldBe "O campo salário é obrigatório"
                }
        }

        @Test
        fun `Should not create a job when requirements is empty`() {
            val jobResponse = testRestTemplate.exchange<ErrorsResponse>(
                RequestEntity.post(URI("/jobs")).body(
                    mapOf(
                        "name" to "name",
                        "description" to "description",
                        "salary" to 1,
                        "requirements" to setOf<RequirementRequest>()
                    )
                )
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.BAD_REQUEST
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .first()
                .should {
                    it.code shouldBe "size"
                    it.description shouldBe "O campo requirements é obrigatório"
                }
        }

        @Test
        fun `Should not create a job when requirements has empty values`() {
            val jobResponse = testRestTemplate.exchange<ErrorsResponse>(
                RequestEntity.post(URI("/jobs")).body(
                    mapOf(
                        "name" to "name",
                        "description" to "description",
                        "salary" to 1,
                        "requirements" to setOf("", "")
                    )
                )
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.BAD_REQUEST
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .first()
                .should {
                    it.code shouldBe "invalid_format"
                    it.description shouldBe "Formato inválido de requirements"
                }
        }

        @Test
        fun `Should not create a job when requirements is null`() {
            val jobResponse = testRestTemplate.exchange<ErrorsResponse>(
                RequestEntity.post(URI("/jobs")).body(
                    mapOf(
                        "name" to "name",
                        "description" to "description",
                        "salary" to 1,
                        "requirements" to null
                    )
                )
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.BAD_REQUEST
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .first()
                .should {
                    it.code shouldBe "size"
                    it.description shouldBe "O campo requirements é obrigatório"
                }
        }

        @Test
        fun `Should create a job when description is empty`() {
            val requirementRequest = RequirementRequest(
                stack = "Kotlin",
                level = LevelRequest(min = 1, max = 2)
            )
            val jobRequest = JobRequest(
                name = "Name",
                description = "",
                salary = 1,
                requirements = setOf(requirementRequest)
            )
            val jobResponse = testRestTemplate.postForEntity<JobResponse>("/jobs", jobRequest)

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.CREATED
            val job = jobResponse.body.shouldNotBeNull()
            job.name shouldBe jobRequest.name
            job.description shouldBe jobRequest.description
            job.salary shouldBe jobRequest.salary
            job.requirements.shouldHaveSize(1).shouldHaveSingleElement(
                RequirementResponse(
                    stack = requirementRequest.stack,
                    level = LevelResponse(
                        min = requirementRequest.level?.min,
                        max = requirementRequest.level?.max
                    )
                )
            )
        }

        @Test
        fun `Should not create a job when requirements not have stack`() {
            val jobResponse = testRestTemplate.exchange<ErrorsResponse>(
                RequestEntity.post(URI("/jobs")).body(
                    mapOf(
                        "name" to "name",
                        "description" to "description",
                        "salary" to 1,
                        "requirements" to setOf(
                            mapOf(
                                "stack" to "",
                                "level" to mapOf(
                                    "min" to 1,
                                    "max" to 2
                                )
                            )
                        )
                    )
                )
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.BAD_REQUEST
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .first()
                .should {
                    it.code shouldBe "size"
                    it.description shouldBe "O campo stack é obrigatório e deve possuir pelo menos 32 caracteres"
                }
        }

        @Test
        fun `Should not create a job when requirements stacks is null`() {
            val jobResponse = testRestTemplate.exchange<ErrorsResponse>(
                RequestEntity.post(URI("/jobs")).body(
                    mapOf(
                        "name" to "name",
                        "description" to "description",
                        "salary" to 1,
                        "requirements" to setOf(
                            mapOf(
                                "stack" to null,
                                "level" to mapOf(
                                    "min" to 1,
                                    "max" to 2
                                )
                            )
                        )
                    )
                )
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.BAD_REQUEST
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .first()
                .should {
                    it.code shouldBe "size"
                    it.description shouldBe "O campo stack é obrigatório e deve possuir pelo menos 32 caracteres"
                }
        }

        @Test
        fun `Should not create a job when requirements stack has more than 32`() {
            val jobResponse = testRestTemplate.exchange<ErrorsResponse>(
                RequestEntity.post(URI("/jobs")).body(
                    mapOf(
                        "name" to "name",
                        "description" to "description",
                        "salary" to 1,
                        "requirements" to setOf(
                            mapOf(
                                "stack" to "a".repeat(33),
                                "level" to mapOf(
                                    "min" to 1,
                                    "max" to 2
                                )
                            )
                        )
                    )
                )
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.BAD_REQUEST
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .first()
                .should {
                    it.code shouldBe "size"
                    it.description shouldBe "O campo stack é obrigatório e deve possuir pelo menos 32 caracteres"
                }
        }

        @Test
        fun `Should create a job when requirements not have level`() {
            val requirementRequest = RequirementRequest(
                stack = "Kotlin"
            )
            val jobRequest = JobRequest(
                name = "Name",
                description = "description",
                salary = 1,
                requirements = setOf(requirementRequest)
            )
            val jobResponse = testRestTemplate.postForEntity<JobResponse>("/jobs", jobRequest)

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.CREATED
            val job = jobResponse.body.shouldNotBeNull()
            job.name shouldBe jobRequest.name
            job.description shouldBe jobRequest.description
            job.salary shouldBe jobRequest.salary
            job.requirements.shouldHaveSize(1).shouldHaveSingleElement(
                RequirementResponse(stack = requirementRequest.stack)
            )
        }

        @Test
        fun `Should not create a job when requirements level min less than 1`() {
            val jobResponse = testRestTemplate.exchange<ErrorsResponse>(
                RequestEntity.post(URI("/jobs")).body(
                    mapOf(
                        "name" to "name",
                        "description" to "description",
                        "salary" to 1,
                        "requirements" to setOf(
                            mapOf(
                                "stack" to "Kotlin",
                                "level" to mapOf(
                                    "min" to 0,
                                    "max" to 2
                                )
                            )
                        )
                    )
                )
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.BAD_REQUEST
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .first()
                .should {
                    it.code shouldBe "min"
                    it.description shouldBe "O tamanho minimo para o campo min é 1"
                }

        }

        @Test
        fun `Should not create a job when requirements level max greater than 100`() {
            val jobResponse = testRestTemplate.exchange<ErrorsResponse>(
                RequestEntity.post(URI("/jobs")).body(
                    mapOf(
                        "name" to "name",
                        "description" to "description",
                        "salary" to 1,
                        "requirements" to setOf(
                            mapOf(
                                "stack" to "Kotlin",
                                "level" to mapOf(
                                    "min" to 1,
                                    "max" to 101
                                )
                            )
                        )
                    )
                )
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.BAD_REQUEST
            jobResponse.body.shouldNotBeNull()
                .errorMessages.shouldHaveSize(1)
                .first()
                .should {
                    it.code shouldBe "max"
                    it.description shouldBe "O tamanho máximo para o campo max é 100"
                }

        }

    }

    @Nested
    inner class DeleteJob {

        @Test
        fun `Should delete job by id and returns 204`() {
            val jobRequest = JobRequest(
                name = "name",
                description = "description",
                salary = 1,
                requirements = setOf(
                    RequirementRequest(
                        stack = "Kotlin",
                        level = LevelRequest(min = 1, max = 2)
                    )
                )
            )
            val createJobResponse = testRestTemplate.postForObject<JobResponse>("/jobs", jobRequest)
            val jobResponse = testRestTemplate.exchange(
                RequestEntity.delete(URI("/jobs/${createJobResponse?.id}")).build(),
                Nothing::class.java
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.NO_CONTENT
        }

        @Test
        fun `Should delete job by id that not exists and returns 404`() {
            val jobResponse = testRestTemplate.exchange(
                RequestEntity.delete(URI("/jobs/${UUID.randomUUID()}")).build(),
                typeOf<ErrorsResponse>()
            )

            jobResponse.shouldNotBeNull()
            jobResponse.statusCode shouldBe HttpStatus.NOT_FOUND
        }
    }

    @Nested
    inner class UpdateJob {

        //not have name
        //not have salary
        //not have requirements
        //big description
        //empty description
        //requirements not have stack
        //requirements not have level
        //requirements level min less than 1
        //requirements level max greater than 100
        //requirements level min greater than max


    }

}