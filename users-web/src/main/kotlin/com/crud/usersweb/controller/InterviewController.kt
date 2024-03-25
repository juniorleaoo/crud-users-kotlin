package com.crud.usersweb.controller

import com.crud.usersweb.service.InterviewService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.UUID

data class InterviewRequest(
    val userId: UUID,
    val jobId: UUID,
    val interviewDate: LocalDateTime
)

@RestController
@RequestMapping("/interviews")
class InterviewController(
    private val interviewService: InterviewService
) {

    @PostMapping
    fun create(@Valid @RequestBody interviewRequest: InterviewRequest): ResponseEntity<Nothing> {
        interviewService.save(interviewRequest.userId, interviewRequest.jobId, interviewRequest.interviewDate)
        return ResponseEntity.ok().build()
    }

}