package com.crud.usersweb.service

import com.crud.usersweb.entity.Interview
import com.crud.usersweb.exceptions.ResourceNotFoundException
import com.crud.usersweb.repository.InterviewRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Transactional
@Service
class InterviewService(
    private val interviewRepository: InterviewRepository,
    private val userService: UserService,
    private val jobService: JobService,
) {
    fun save(userId: UUID, jobId: UUID, date: LocalDateTime) {
        val user = userService.findById(userId).orElseThrow { ResourceNotFoundException("User not found") }
        val job = jobService.findById(jobId).orElseThrow { ResourceNotFoundException("Job not found") }
        interviewRepository.save(Interview(user, job, date))
    }

}