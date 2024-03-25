package com.crud.usersweb.controller.request

import com.crud.usersweb.entity.Job
import com.crud.usersweb.entity.Requirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class JobRequest(
    @field:Size(min = 1, max = 500, message = "O campo nome é obrigatório e deve estar entre 1 e 500")
    val name: String,
    @field:NotBlank
    val description: String,
    @field:NotNull
    @field:Digits(integer = 6, fraction = 2)
    val salary: BigDecimal,
    @field:Valid
    val requirements: Set<RequirementRequest>
)

data class RequirementRequest(
    @field:Size(min = 1, max = 32, message = "O campo stack é obrigatório e deve possuir pelo menos 32 caracteres")
    val stack: String,
    @field:Valid
    val level: LevelRequest
)

data class LevelRequest(
    @field:Min(value = 1, message = "O tamanho minimo para o campo min é 1")
    val min: Int,
    @field:Max(value = 100, message = "O tamanho máximo para o campo max é 100")
    val max: Int
)

fun JobRequest.toJob(): Job {
    val job = Job(
        name = name,
        description = description,
        salary = salary
    )
    requirements.forEach {
        job.requirements.add(
            Requirement(
                id = null,
                stack = it.stack,
                min = it.level.min,
                max = it.level.max,
                job = job
            )
        )
    }
    return job
}