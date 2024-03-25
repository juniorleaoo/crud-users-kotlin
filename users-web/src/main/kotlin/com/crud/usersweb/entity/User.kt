package com.crud.usersweb.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id
    @UuidGenerator
    val id: UUID?,

    @Column(name = "nick", length = 32)
    val nick: String?,

    @Column(name = "name", length = 255, unique = true, nullable = false)
    val name: String,

    @Column(name = "birth_date", nullable = false)
    val birthDate: LocalDateTime,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val stack: MutableSet<Stack>?,
) {

    constructor(id: UUID) : this(id, null, "", LocalDateTime.now(), null)

    override fun equals(other: Any?): Boolean {
        if (other is User){
            return if(other.id != null && id != null) id == other.id else super.equals(other)
        }

        return false
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: super.hashCode()
    }

}