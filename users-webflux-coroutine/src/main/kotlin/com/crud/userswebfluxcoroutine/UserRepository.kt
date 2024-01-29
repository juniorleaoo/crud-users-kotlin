package com.crud.userswebfluxcoroutine

import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface UserRepository {

    suspend fun findById(id: UUID): User?

    fun findAll(): Flow<User>

    suspend fun insert(user: User): User?

    suspend fun update(user: User): User?

    suspend fun existsById(id: UUID): Boolean

    suspend fun deleteById(id: UUID)

}