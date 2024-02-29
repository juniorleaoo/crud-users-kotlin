package com.crud.usersweb.repository

import com.crud.usersweb.entity.Stack
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StackRepository: JpaRepository<Stack, UUID> {

    @Modifying
    @Query("DELETE FROM Stack WHERE user.id = ?1")
    fun deleteAllByUserId(userId: UUID)

    @Query("SELECT * FROM stacks WHERE user_id = ?1", nativeQuery = true)
    fun findAllByUserId(userId: UUID): List<Stack>

}