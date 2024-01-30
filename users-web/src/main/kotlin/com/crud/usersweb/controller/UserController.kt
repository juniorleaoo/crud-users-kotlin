package com.crud.usersweb.controller

import com.crud.usersweb.exceptions.ResourceNotFoundException
import com.crud.usersweb.service.UserService
import jakarta.validation.Valid
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
    private val userService: UserService
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") id: UUID): ResponseEntity<UserResponse> {
        val user = userService.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found") }
        return ResponseEntity.ok(user.toUserResponse())
    }

    @GetMapping
    fun listUsers(): ResponseEntity<List<UserResponse>> {
        val users = userService.findAll()
        return ResponseEntity.ok(users.map { it.toUserResponse() })
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable("id") id: UUID): ResponseEntity<Nothing> {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build()
        }
        userService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    fun createUser(@Valid @RequestBody createUserRequest: CreateUserRequest): ResponseEntity<UserResponse> {
        val user = createUserRequest.toUser()
        val userCreated = userService.save(user)

        val httpHeaders = HttpHeaders()
        httpHeaders.location = URI.create("/users/${userCreated.id}")
        return ResponseEntity(userCreated.toUserResponse(), httpHeaders, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable("id") id: UUID,
        @Valid @RequestBody updateUserRequest: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val userUpdated = userService.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found") }
            .copy(
                nick = updateUserRequest.nick,
                name = updateUserRequest.name,
                birthDate = updateUserRequest.birthDate,
                stack = updateUserRequest.stack,
            )
            .run { userService.save(this) }
        return ResponseEntity.ok(userUpdated.toUserResponse())
    }

}