package com.crud.userswebfluxcoroutine

import io.r2dbc.spi.Readable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.util.UUID

@Repository
class UserDatabaseClientRepository(
    val client: DatabaseClient,
) : UserRepository {

    private fun rowToUser(row: Readable): User {
        val bb = ByteBuffer.wrap(row.get("id", ByteArray::class.java))
        return User(
            id = UUID(bb.getLong(), bb.getLong()),
            name = row.get("name", String::class.java)!!,
            nick = row.get("nick", String::class.java),
            birthDate = row.get("birth_date", LocalDateTime::class.java)!!,
            stack = StringListConverter.convertToEntityAttribute(row.get("stack", String::class.java))
        )
    }

    private fun userToMap(user: User) = mapOf(
        "id" to UUIDWriteConverter().convert(user.id!!),
        "nick" to user.nick,
        "name" to user.name,
        "birthDate" to user.birthDate,
        "stack" to StringListConverter.convertToDatabaseColumn(user.stack),
    )

    override suspend fun findById(id: UUID): User? {
        return client.sql("SELECT * FROM users WHERE id = :id")
            .bind("id", UUIDWriteConverter().convert(id))
            .map { row -> rowToUser(row) }
            .first()
            .awaitFirstOrNull()
    }

    override fun findAll(): Flow<User> {
        return client.sql("SELECT * FROM users")
            .map { row -> rowToUser(row) }
            .flow()
    }

    override suspend fun insert(user: User): User? {
        user.id = UUID.randomUUID()
        client.sql { "INSERT INTO users (id, nick, name, birth_date, stack) VALUES (:id, :nick, :name, :birthDate, :stack)" }
            .bindValues(userToMap(user))
            .fetch()
            .awaitSingleOrNull()

        return findById(user.id!!)
    }

    override suspend fun update(user: User): User? {
        client.sql { "UPDATE users SET nick = :nick, name = :name, birth_date = :birthDate, stack = :stack WHERE id = :id" }
            .bindValues(userToMap(user))
            .fetch()
            .first()
            .awaitFirstOrNull()

        return findById(user.id!!)
    }

    override suspend fun existsById(id: UUID): Boolean {
        val exists = client.sql("SELECT COUNT(*) FROM users WHERE id = :id")
            .bind("id", UUIDWriteConverter().convert(id))
            .map { row -> row.get(0, String::class.java)?.toLong()!! > 0 }
            .first()
            .awaitSingle()

        return exists ?: false
    }

    override suspend fun deleteById(id: UUID) {
        client.sql("DELETE FROM users WHERE id = :id")
            .bind("id", UUIDWriteConverter().convert(id))
            .fetch()
            .rowsUpdated()
            .awaitSingle()
    }

}