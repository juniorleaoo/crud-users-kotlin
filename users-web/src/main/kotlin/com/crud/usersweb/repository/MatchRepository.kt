package com.crud.usersweb.repository

import com.crud.usersweb.entity.Interview
import com.crud.usersweb.entity.Job
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MatchRepository : JpaRepository<Interview, UUID>{

    @Query(value = """
        SELECT DISTINCT job.id, job.name AS job_name, job.description, job.salary
            FROM users user
            JOIN stacks stk ON user.id = stk.user_id
            JOIN jobs job ON job.id IN (
                SELECT job_id
                FROM requirements req
                WHERE stk.name = req.stack
                AND stk.score >= req.min
                AND (req.max IS NULL OR stk.score <= req.max)
            )
            WHERE user.id = ?1;
    """, nativeQuery = true)
    fun findAllJobsMatchByUser(userId: UUID): List<Job>

//    fun findAllUsersMatchByJob(jobId: UUID, pagination: Pagination): Page<User>

}
