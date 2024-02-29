package com.crud.usersweb.entity

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(
    name = "stacks",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "name"])]
)
data class Stack(
    @Id
    @UuidGenerator
    val id: UUID?,

    @Column(name = "name", length = 32, nullable = false)
    val name: String,

    @Column(name = "score", nullable = false)
    val level: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
) {

    override fun equals(other: Any?): Boolean {
        if (other is Stack){
            return if(other.id != null && id != null) id == other.id else super.equals(other)
        }
        return false
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: super.hashCode()
    }

}