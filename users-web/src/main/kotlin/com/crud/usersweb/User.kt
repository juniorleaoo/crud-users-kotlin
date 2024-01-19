package com.crud.usersweb

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Column(name = "nick", length = 32)
    var nick: String?,

    @Column(name = "name", length = 255, unique = true, nullable = false)
    var name: String,

    @Column(name = "birth_date", nullable = false)
    var birthDate: LocalDateTime,

    @Convert(converter = StringListConverter::class)
    @Lob
    @Column(name = "stack")
    var stack: List<String>?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID
}