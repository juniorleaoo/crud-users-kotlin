package com.crud.usersweb.controller

import com.crud.usersweb.controller.request.JobRequest
import com.crud.usersweb.controller.request.toJob
import com.crud.usersweb.controller.response.JobResponse
import com.crud.usersweb.controller.response.PageResponse
import com.crud.usersweb.controller.response.toJobResponse
import com.crud.usersweb.controller.response.toPageResponse
import com.crud.usersweb.entity.Pagination
import com.crud.usersweb.exceptions.ResourceNotFoundException
import com.crud.usersweb.service.JobService
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/jobs")
class JobController(
    private val jobService: JobService
) {

    @GetMapping("/{id}")
    fun getJob(@PathVariable("id") id: UUID): ResponseEntity<JobResponse> {
        val job = jobService.findById(id)
            .orElseThrow { ResourceNotFoundException("Job not found") }
        return ResponseEntity.ok(job.toJobResponse())
    }

    @GetMapping
    fun listJobs(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("page_size", defaultValue = "15") pageSize: Int,
        @RequestParam("sort", defaultValue = "id") sort: String,
    ): ResponseEntity<PageResponse<JobResponse>> {
        val jobs = jobService.findAll(
            Pagination(
                page,
                pageSize,
                sort
            )
        )
        val jobPageResponse = jobs.toPageResponse { it.toJobResponse() }
        return if (jobs.hasNext()) {
            ResponseEntity(jobPageResponse, HttpStatus.PARTIAL_CONTENT)
        } else {
            ResponseEntity.ok(jobPageResponse)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteJob(@PathVariable("id") id: UUID): ResponseEntity<Nothing> {
        if (!jobService.existsById(id)) {
            return ResponseEntity.notFound().build()
        }
        jobService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    fun createJob(@Valid @RequestBody jobRequest: JobRequest): ResponseEntity<JobResponse> {
        val job = jobRequest.toJob()
        val jobCreated = jobService.save(job)

        val httpHeaders = HttpHeaders()
        httpHeaders.location = URI.create("/jobs/${jobCreated.id}")
        return ResponseEntity(jobCreated.toJobResponse(), httpHeaders, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateJob(
        @PathVariable("id") id: UUID,
        @Valid @RequestBody jobRequest: JobRequest
    ): ResponseEntity<JobResponse> {
        val jobUpdated = jobService.update(id, jobRequest.toJob())
        return ResponseEntity.ok(jobUpdated.toJobResponse())
    }

}