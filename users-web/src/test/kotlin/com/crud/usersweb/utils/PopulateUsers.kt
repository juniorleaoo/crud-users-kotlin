package com.crud.usersweb.utils

import com.crud.usersweb.entity.User
import com.crud.usersweb.repository.UserRepository
import java.time.LocalDateTime

class PopulateUsers(
    private val userRepository: UserRepository
) {

    fun createUser(amount: Long) {
        for (i in 1..amount) {
            userRepository.save(
                User(
                    id = null,
                    nick = "Nick $i",
                    name = "Name $i",
                    birthDate = LocalDateTime.now(),
                    stack = mutableSetOf()
                )
            )
        }
    }

}