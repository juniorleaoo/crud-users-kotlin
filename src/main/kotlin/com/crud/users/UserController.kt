package com.crud.users

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
    fun getUser(@PathVariable("id") id: UUID): ResponseEntity<User> {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found") }
        return ResponseEntity.ok(user)
    }

    @GetMapping
    fun listUsers(): ResponseEntity<MutableIterable<User>> {
        val users = userRepository.findAll()
        return ResponseEntity.ok(users)
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
    fun createUser(@RequestBody createUserRequest: CreateUserRequest): ResponseEntity<CreateUserResponse> {
        val user = User(
            UUID.randomUUID(),
            nick = createUserRequest.nick,
            name = createUserRequest.name,
            birthDate = createUserRequest.birthDate,
            stack = createUserRequest.stack
        )
        val userCreated = userRepository.save(user)

        val httpHeaders = HttpHeaders()
        httpHeaders.location = URI.create("/users/${userCreated.id}")
        return ResponseEntity(CreateUserResponse(userCreated), httpHeaders, HttpStatus.CREATED)
    }

}