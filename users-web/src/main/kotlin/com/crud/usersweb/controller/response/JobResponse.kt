package com.crud.usersweb.controller.response

import com.crud.usersweb.entity.Job
import java.util.UUID

data class JobResponse(
    val id: UUID?,
    val name: String,
    val description: String,
    val salary: Int,
    val requirements: Set<RequirementResponse>
)

data class RequirementResponse(
    val stack: String,
    val level: LevelResponse? = null
)

data class LevelResponse(
    val min: Int?,
    val max: Int?
)

fun Job.toJobResponse(): JobResponse {
    return JobResponse(
        id = id,
        name = name,
        description = description,
        salary = salary,
        requirements = requirements.map {
            RequirementResponse(
                stack = it.stack,
                level = if (it.min == null && it.max == null) null else LevelResponse(it.min, it.max)
            )
        }.toSet()
    )
}