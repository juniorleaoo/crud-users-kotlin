package com.crud.usersweb.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
@Table(name = "jobs")
data class Job(
    @Id
    @UuidGenerator
    val id: UUID?,

    @Column(name = "name", length = 500, nullable = false)
    val name: String,

    @Lob
    @Column(name = "description")
    val description: String,

    @Column(name = "salary", nullable = false)
    val salary: Int,

    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val requirements: MutableSet<Requirement>,
) {

    constructor(id: UUID) : this(id, "", "", 0, mutableSetOf())

    constructor(name: String, description: String, salary: Int) : this(
        null,
        name,
        description,
        salary,
        mutableSetOf()
    )

    override fun equals(other: Any?): Boolean {
        if (other is Job) {
            return if (other.id != null && id != null) id == other.id else super.equals(other)
        }

        return false
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: super.hashCode()
    }

}