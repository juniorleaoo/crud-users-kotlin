package com.crud.usersweb.utils

import com.crud.usersweb.entity.Job
import com.crud.usersweb.entity.User
import com.crud.usersweb.repository.JobRepository
import com.crud.usersweb.repository.UserRepository
import java.math.BigDecimal
import java.time.LocalDateTime

class PopulateUsers(
    private val userRepository: UserRepository
) {

    fun createUser(amount: Long) {
        for (i in 1..amount) {
            userRepository.save(
                User(
                    id = null,
                    nick = "Nick $i",
                    name = "Name $i",
                    birthDate = LocalDateTime.now(),
                    stack = mutableSetOf()
                )
            )
        }
    }

}

class PopulateJobs(
    private val jobRepository: JobRepository
) {

    fun createJob(amount: Long) {
        for (i in 1..amount) {
            jobRepository.save(
                Job(
                    id = null,
                    name = "Job $i",
                    description = "Description $i",
                    salary = BigDecimal.valueOf(i),
                    requirements = mutableSetOf()
                )
            )
        }
    }

}