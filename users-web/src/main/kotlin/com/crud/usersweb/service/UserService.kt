package com.crud.usersweb.service

import com.crud.usersweb.entity.Pagination
import com.crud.usersweb.entity.User
import com.crud.usersweb.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun findById(id: UUID) = userRepository.findById(id)

    fun findAll(): MutableIterable<User> = userRepository.findAll()

    fun findAll(pagination: Pagination): Page<User> {
        return userRepository.findAll(pagination.pageable)
    }

    fun count(): Long = userRepository.count()

    fun existsById(id: UUID): Boolean = userRepository.existsById(id)

    fun deleteById(id: UUID) = userRepository.deleteById(id)

    fun save(user: User): User = userRepository.save(user)

}