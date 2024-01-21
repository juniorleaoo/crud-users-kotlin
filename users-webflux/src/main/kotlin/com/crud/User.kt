package com.crud

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID?,

    @Column(name = "nick", length = 32)
    val nick: String?,

    @Column(name = "name", length = 255, unique = true, nullable = false)
    val name: String,

    @Column(name = "birth_date", nullable = false)
    val birthDate: LocalDateTime?,

    @Convert(converter = StringListConverter::class)
    @Lob
    @Column(name = "stack")
    val stack: List<String>?,
) {
    constructor() : this(null, null, "", null, null)

}