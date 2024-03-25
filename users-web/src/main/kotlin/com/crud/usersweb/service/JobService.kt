package com.crud.usersweb.service

import com.crud.usersweb.entity.Job
import com.crud.usersweb.entity.Pagination
import com.crud.usersweb.entity.Requirement
import com.crud.usersweb.exceptions.ResourceNotFoundException
import com.crud.usersweb.repository.JobRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class JobService(
    private val jobRepository: JobRepository
) {

    fun findById(id: UUID) = jobRepository.findById(id)

    fun findAll(pagination: Pagination): Page<Job> {
        if(count() > 0){
            return jobRepository.findAll(pagination.pageable)
        }
        return Page.empty(pagination.pageable)
    }

    fun count(): Long = jobRepository.count()

    fun existsById(id: UUID): Boolean = jobRepository.existsById(id)

    fun deleteById(id: UUID) = jobRepository.deleteById(id)

    fun save(job: Job): Job = jobRepository.save(job)

    fun update(id: UUID, job: Job): Job {
        val jobDB = jobRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Job not found") }

        val requirements = job.requirements.map {
            Requirement(
                id = it.id,
                stack = it.stack,
                min = it.min,
                max = it.max,
                job = jobDB
            )
        }.toMutableSet()

        return jobRepository.save(
            jobDB.copy(
                name = job.name,
                description = job.description,
                salary = job.salary,
                requirements = requirements
            )
        )
    }

}