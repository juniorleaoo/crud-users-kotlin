package com.crud.usersweb.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "interviews")
data class Interview(
    @Id
    @UuidGenerator
    val id: UUID?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false, updatable = false)
    val job: Job,

    @Column(name = "interview_date", nullable = false)
    val date: LocalDateTime,
) {

    constructor(user: User, job: Job, date: LocalDateTime) : this(
        id = null,
        user = user,
        job = job,
        date
    )
}