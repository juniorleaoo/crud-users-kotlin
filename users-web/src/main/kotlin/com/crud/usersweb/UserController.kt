package com.crud.usersweb

import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userRepository: UserRepository
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") id: UUID): ResponseEntity<UserResponse> {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found") }
        return ResponseEntity.ok(user.toUserResponse())
    }

    @GetMapping
    fun listUsers(): ResponseEntity<List<UserResponse>> {
        val users = userRepository.findAll()
        return ResponseEntity.ok(users.map { it.toUserResponse() })
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable("id") id: UUID): ResponseEntity<Nothing> {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build()
        }
        userRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    fun createUser(@Valid @RequestBody createUserRequest: CreateUserRequest): ResponseEntity<UserResponse> {
        val user = createUserRequest.toUser()
        val userCreated = userRepository.save(user)

        val httpHeaders = HttpHeaders()
        httpHeaders.location = URI.create("/users/${userCreated.id}")
        return ResponseEntity(userCreated.toUserResponse(), httpHeaders, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable("id") id: UUID,
        @Valid @RequestBody updateUserRequest: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val userUpdated = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found") }
            .apply {
                updateUserRequest.nick.also { nick = it }
                updateUserRequest.name?.also { name = it }
                updateUserRequest.birthDate?.also { birthDate = it }
                updateUserRequest.stack.also { stack = it }
            }
            .run { userRepository.save(this) }

        return ResponseEntity.ok(userUpdated.toUserResponse())
    }

}