package com.crud.users

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

class ErrorResponse(
    val error: String,
    val error_description: String
)

class ErrorsResponse(
    val errors: List<ErrorResponse>
)

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun resourceNotFoundException(
        req: HttpServletRequest,
        exception: ResourceNotFoundException
    ): ResponseEntity<Nothing> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArguentNotValidException(
        req: HttpServletRequest,
        exception: MethodArgumentNotValidException
    ): ResponseEntity<ErrorsResponse> {
        val errors = exception.bindingResult.allErrors.map {
            ErrorResponse(
                error = it.defaultMessage ?: "",
                error_description = ""
            )
        }

        return ResponseEntity.badRequest().body(ErrorsResponse(errors))
    }

}