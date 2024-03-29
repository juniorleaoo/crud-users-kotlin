package com.crud.usersweb.repository

import com.crud.usersweb.entity.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository: CrudRepository<User, UUID>