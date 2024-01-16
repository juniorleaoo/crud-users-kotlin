package com.crud.users

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID,

    @Column(name = "nick", length = 32, unique = true, nullable = false)
    val nick: String,

    @Column(name = "name", length = 255, nullable = false)
    var name: String,

    @Column(name = "birth_date", nullable = false)
    val birthDate: LocalDate,

    @Convert(converter = StringListConverter::class)
    @Column(name = "stack", columnDefinition = "text", nullable = false)
    val stack: List<String>?,
)