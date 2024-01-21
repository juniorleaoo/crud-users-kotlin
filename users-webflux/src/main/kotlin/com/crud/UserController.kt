package com.crud

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userRepository: UserRepository
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") id: UUID): Mono<ResponseEntity<User>> {
        return Mono.fromCallable {
            val user = userRepository.findById(id)
                .orElseThrow { ResourceNotFoundException("User not found") }
            ResponseEntity.ok(user)
        }.onErrorResume { throw ResourceNotFoundException("User not found") }
    }

    @GetMapping
    fun listUsers(): Mono<ResponseEntity<List<User>>> {
        return Mono.fromCallable {
            ResponseEntity.ok(userRepository.findAll().map {
                it
            })
        }
    }

}