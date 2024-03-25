package com.crud.usersweb.service

import com.crud.usersweb.entity.Job
import com.crud.usersweb.entity.Pagination
import com.crud.usersweb.entity.User
import com.crud.usersweb.exceptions.ResourceNotFoundException
import com.crud.usersweb.repository.MatchRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class MatchService(
    private val jobService: JobService,
    private val userService: UserService,
    private val matchRepository: MatchRepository,
) {

    fun findAllJobsMatchByUser(userId: UUID, pagination: Pagination): Page<Job> {
        userService.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found") }
        return Page.empty()
//        matchRepository.findAllJobsMatchByUser(userId, pagination)
    }

    fun findAllUsersMatchByJob(jobId: UUID, pagination: Pagination): Page<User> {
        val job = jobService.findById(jobId)
            .orElseThrow { ResourceNotFoundException("Job not found") }
        return Page.empty()
    }
}