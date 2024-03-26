package com.crud.usersweb.controller.request

import com.crud.usersweb.entity.Job
import com.crud.usersweb.entity.Requirement
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class JobRequest(
    @get:Size(min = 1, max = 500, message = "O campo nome é obrigatório e deve estar entre 1 e 500")
    val name: String,

    val description: String,

    @get:NotNull(message = "O campo salário é obrigatório")
    @get:Min(value = 1, message = "O campo salário deve ser maior que 0")
    val salary: Int,

    @get:Valid
    @get:Size(min = 1, message = "O campo requirements é obrigatório")
    val requirements: Set<RequirementRequest>
)

data class RequirementRequest(
    @get:Size(min = 1, max = 32, message = "O campo stack é obrigatório e deve possuir pelo menos 32 caracteres")
    val stack: String,
    @get:Valid
    val level: LevelRequest? = null
)

data class LevelRequest(
    @get:Min(value = 1, message = "O tamanho minimo para o campo min é 1")
    val min: Int,
    @get:Max(value = 100, message = "O tamanho máximo para o campo max é 100")
    val max: Int
) {
    init {
        require(min <= max) { "O valor mínimo deve ser menor ou igual ao valor máximo" }
    }

}

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
                min = it.level?.min,
                max = it.level?.max,
                job = job
            )
        )
    }
    return job
}