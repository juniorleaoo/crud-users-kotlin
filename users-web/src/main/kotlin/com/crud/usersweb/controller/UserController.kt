package com.crud.usersweb.controller

import com.crud.usersweb.entity.Pagination
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
import org.springframework.web.bind.annotation.RequestParam
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

    @GetMapping("/{id}/stacks")
    fun listStacks(@PathVariable("id") id: UUID): ResponseEntity<List<StackResponse>> {
        val stacks = userService.findAllStacksByUserId(id)
        return ResponseEntity.ok(stacks.map { StackResponse(it) }.toList())
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
    fun createUser(@Valid @RequestBody userRequest: UserRequest): ResponseEntity<UserResponse> {
        val user = userRequest.toUser()
        val userCreated = userService.save(user)

        val httpHeaders = HttpHeaders()
        httpHeaders.location = URI.create("/users/${userCreated.id}")
        return ResponseEntity(userCreated.toUserResponse(), httpHeaders, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable("id") id: UUID,
        @Valid @RequestBody userRequest: UserRequest
    ): ResponseEntity<UserResponse> {
        val userUpdated = userService.update(id, userRequest.toUser())
        return ResponseEntity.ok(userUpdated.toUserResponse())
    }

}