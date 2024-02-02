package com.crud.usersweb.controller

import com.crud.usersweb.entity.Pagination
import com.crud.usersweb.exceptions.ResourceNotFoundException
import com.crud.usersweb.service.UserService
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
    private val userService: UserService
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") id: UUID): ResponseEntity<UserResponse> {
        val user = userService.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found") }
        return ResponseEntity.ok(user.toUserResponse())
    }

    @GetMapping
    fun listUsers(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("page_size", defaultValue = "15") pageSize: Int,
        @RequestParam("sort", defaultValue = "id") sort: String,
    ): ResponseEntity<PageResponse<UserResponse>> {
        val users = userService.findAll(
            Pagination(
                page,
                pageSize,
                sort
            )
        )
        val userPageResponse = users.toPageResponse { it.toUserResponse() }
        return if (users.hasNext()) {
            ResponseEntity(userPageResponse, HttpStatus.PARTIAL_CONTENT)
        } else {
            ResponseEntity.ok(userPageResponse)
        }
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