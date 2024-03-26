package com.crud.usersweb.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
@Table(name = "requirements")
data class Requirement(
    @Id
    @UuidGenerator
    val id: UUID?,

    @Column(name = "stack", length = 500, nullable = false)
    val stack: String,

    @Column(name = "min")
    val min: Int?,

    @Column(name = "max")
    val max: Int?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    val job: Job
) {

    override fun equals(other: Any?): Boolean {
        if (other is Requirement){
            return if(other.id != null && id != null) id == other.id else super.equals(other)
        }

        return false
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: super.hashCode()
    }

}