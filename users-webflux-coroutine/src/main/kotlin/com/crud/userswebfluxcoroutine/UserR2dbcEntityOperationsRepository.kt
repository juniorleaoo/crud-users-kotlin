package com.crud.userswebfluxcoroutine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.delete
import org.springframework.data.r2dbc.core.flow
import org.springframework.data.r2dbc.core.insert
import org.springframework.data.r2dbc.core.select
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Repository
import java.util.UUID
import kotlin.math.absoluteValue

@Repository
class UserR2dbcEntityOperationsRepository(
    val r2dbcEntityOperations: R2dbcEntityOperations
) : UserRepository {

    override suspend fun findById(id: UUID): User? {
        return r2dbcEntityOperations.select<User>()
            .from("users")
            .matching(query(where("id").`is`(id.toByteArray())))
            .first()
            .awaitFirstOrNull()
    }

    override fun findAll(): Flow<User> {
        return r2dbcEntityOperations.select<User>()
            .from("users")
            .flow()
    }

    override suspend fun insert(user: User): User? {
        user.id = UUID.randomUUID()
        return r2dbcEntityOperations.insert<User>()
            .into("users")
            .using(user)
            .awaitFirstOrNull()
    }

    override suspend fun update(user: User): User? {
        val query = r2dbcEntityOperations.update(User::class.java)
            .inTable("users")
            .matching(query(where("id").`is`(user.id!!.toByteArray())))
            .apply(Update.update("nick", user.nick)
                .set("name", user.name)
                .set("birth_date", user.birthDate)
                .set("stack", user.stack))

        r2dbcEntityOperations.update(query)
            .awaitFirstOrNull()

        return findById(user.id!!)
    }

    override suspend fun existsById(id: UUID): Boolean {
        return r2dbcEntityOperations.count(
            query(where("id").`is`(id.toByteArray())),
            User::class.java
        )
            .map { row -> row.absoluteValue > 0 }
            .awaitSingle()
    }

    override suspend fun deleteById(id: UUID) {
        r2dbcEntityOperations.delete<User>()
            .from("users")
            .matching(query(where("id").`is`(id.toByteArray())))
            .all()
            .awaitSingle()
    }

}
