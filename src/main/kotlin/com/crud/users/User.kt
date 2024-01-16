package com.crud.users

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Column(name = "nick", length = 32, unique = true, nullable = false)
    var nick: String,

    @Column(name = "name", length = 255, nullable = false)
    var name: String,

    @Column(name = "birth_date", nullable = false)
    var birthDate: LocalDate,

    @Convert(converter = StringListConverter::class)
    @Column(name = "stack", columnDefinition = "text", nullable = false)
    var stack: List<String>?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID
}