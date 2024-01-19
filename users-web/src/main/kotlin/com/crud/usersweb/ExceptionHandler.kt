package com.crud.usersweb

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

data class ErrorsResponse(
    var errors: List<String>
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
        val response = ErrorsResponse(mutableListOf())
        response.errors = exception.bindingResult.allErrors.map { it.defaultMessage ?: "" }
        return ResponseEntity.badRequest().body(response)
    }

}