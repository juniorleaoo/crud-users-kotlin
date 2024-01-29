package com.crud.userswebfluxcoroutine

import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    @Qualifier("userDatabaseClientRepository") private val userRepository: UserRepository
) {

    @GetMapping("/{id}")
    suspend fun getUser(@PathVariable("id") id: UUID): ResponseEntity<UserResponse> {
        val user = userRepository.findById(id) ?: throw ResourceNotFoundException("User not found")
        return ResponseEntity.ok(user.toUserResponse())
    }

    @GetMapping
    suspend fun listUsers(): ResponseEntity<Flow<UserResponse>> {
        val users = userRepository.findAll()
            .map { it.toUserResponse() }
        return ResponseEntity.ok(users)
    }

    @PostMapping
    suspend fun createUser(@Valid @RequestBody createUserRequest: CreateUserRequest): ResponseEntity<UserResponse> {
        val user = createUserRequest.toUser()
        val userCreated = userRepository.insert(user)

        val httpHeaders = HttpHeaders()
        httpHeaders.location = URI.create("/users/${userCreated?.id}")
        return ResponseEntity(userCreated?.toUserResponse(), httpHeaders, HttpStatus.CREATED)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteUser(@PathVariable("id") id: UUID): ResponseEntity<Nothing> {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build()
        }
        userRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    suspend fun updateUser(
        @PathVariable("id") id: UUID,
        @Valid @RequestBody updateUserRequest: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val userUpdated = userRepository.findById(id) ?: throw ResourceNotFoundException("User not found")
        userUpdated.apply {
            updateUserRequest.nick.also { nick = it }
            updateUserRequest.name?.also { name = it }
            updateUserRequest.birthDate?.also { birthDate = it }
            updateUserRequest.stack.also { stack = it }
        }.run { userRepository.update(this) }

        return ResponseEntity.ok(userUpdated.toUserResponse())
    }

}