package com.crud.usersweb.service

import com.crud.usersweb.entity.Pagination
import com.crud.usersweb.entity.Stack
import com.crud.usersweb.entity.User
import com.crud.usersweb.exceptions.ResourceNotFoundException
import com.crud.usersweb.repository.StackRepository
import com.crud.usersweb.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@Service
class UserService(
    private val userRepository: UserRepository,
    private val stackRepository: StackRepository,
) {

    fun findById(id: UUID) = userRepository.findById(id)

    fun findAll(): MutableIterable<User> = userRepository.findAll()

    fun findAll(pagination: Pagination): Page<User> {
        if(count() > 0){
            return userRepository.findAll(pagination.pageable)
        }
        return Page.empty(pagination.pageable)
    }

    fun count(): Long = userRepository.count()

    fun existsById(id: UUID): Boolean = userRepository.existsById(id)

    fun deleteById(id: UUID) = userRepository.deleteById(id)

    fun save(user: User): User = userRepository.save(user)

    fun update(id: UUID, user: User): User {
        val userDB = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found") }

        val stack = user.stack?.map {
            Stack(
                id = it.id,
                name = it.name,
                level = it.level,
                user = userDB
            )
        }?.toMutableSet()

        return userDB.copy(
            nick = user.nick,
            name = user.name,
            birthDate = user.birthDate,
            stack = stack
        ).run {
            stackRepository.deleteAllByUserId(this.id!!)
            userRepository.save(this)
        }
    }

    fun findAllStacksByUserId(id: UUID): List<Stack> {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found") }
            .run {
                stackRepository.findAllByUserId(this.id!!)
            }
    }

}