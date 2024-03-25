package com.crud.usersweb.controller

import com.crud.usersweb.controller.response.JobResponse
import com.crud.usersweb.controller.response.PageResponse
import com.crud.usersweb.controller.response.UserResponse
import com.crud.usersweb.controller.response.toJobResponse
import com.crud.usersweb.controller.response.toPageResponse
import com.crud.usersweb.controller.response.toUserResponse
import com.crud.usersweb.entity.Pagination
import com.crud.usersweb.service.MatchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping
class MatchController(
    private val matchService: MatchService
) {

    @GetMapping("/users/{id}/jobs/match")
    fun jobsMatchByUser(
        @PathVariable("id") id: UUID,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("page_size", defaultValue = "15") pageSize: Int,
        @RequestParam("sort", defaultValue = "id") sort: String,
    ): ResponseEntity<PageResponse<JobResponse>> {
        val jobs = matchService.findAllJobsMatchByUser(
            id,
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

    @GetMapping("/jobs/{id}/match")
    fun usersMatchByJob(
        @PathVariable("id") id: UUID,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("page_size", defaultValue = "15") pageSize: Int,
        @RequestParam("sort", defaultValue = "id") sort: String,
    ): ResponseEntity<PageResponse<UserResponse>> {
        val users = matchService.findAllUsersMatchByJob(
            id,
            Pagination(
                page,
                pageSize,
                sort
            )
        )
        val userPageResponse = users.toPageResponse { it.toUserResponse() }
        return if (users.hasNext()) {
            ResponseEntity(userPageResponse, HttpStatus.PARTIAL_CONTENT)
        } else {
            ResponseEntity.ok(userPageResponse)
        }
    }

}