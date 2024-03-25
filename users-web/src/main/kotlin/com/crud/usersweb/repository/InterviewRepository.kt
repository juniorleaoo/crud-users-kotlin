package com.crud.usersweb.repository

import com.crud.usersweb.entity.Interview
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface InterviewRepository : JpaRepository<Interview, UUID> {
}