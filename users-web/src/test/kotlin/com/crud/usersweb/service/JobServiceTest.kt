package com.crud.usersweb.service

import com.crud.usersweb.repository.JobRepository
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Tag("unit")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class JobServiceTest {

    @Autowired
    lateinit var jobService: JobService

    @MockK
    lateinit var jobRepository: JobRepository

    @Nested
    inner class GetJobById {
    }

    @Nested
    inner class GetAllJobs {
    }

}