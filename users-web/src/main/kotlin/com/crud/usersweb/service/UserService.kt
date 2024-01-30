package com.crud.usersweb.service

import com.crud.usersweb.entity.User
import com.crud.usersweb.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun findById(id: UUID) = userRepository.findById(id)

    fun findAll(): MutableIterable<User> = userRepository.findAll()

    fun existsById(id: UUID): Boolean = userRepository.existsById(id)

    fun deleteById(id: UUID) = userRepository.deleteById(id)

    fun save(user: User): User = userRepository.save(user)

}