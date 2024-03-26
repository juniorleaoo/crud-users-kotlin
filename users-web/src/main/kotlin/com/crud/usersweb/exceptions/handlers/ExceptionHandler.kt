package com.crud.usersweb.exceptions.handlers

import com.crud.usersweb.exceptions.APIErrorEnum
import com.crud.usersweb.exceptions.APIErrorEnum.DATE_TIME_INVALID_FORMAT
import com.crud.usersweb.exceptions.APIErrorEnum.INVALID_FORMAT
import com.crud.usersweb.exceptions.ResourceNotFoundException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.format.DateTimeParseException

data class ErrorsResponse(
    var errorMessages: List<ErrorMessage> = mutableListOf()
) {
    constructor(errorMessage: ErrorMessage) : this(listOf(errorMessage))

    constructor(apiError: APIErrorEnum) : this(
        listOf(
            ErrorMessage(
                code = apiError.code,
                description = apiError.description
            )
        )
    )
}

data class ErrorMessage(
    val code: String,
    val description: String
)

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun resourceNotFoundException(
        req: HttpServletRequest,
        exception: ResourceNotFoundException
    ): ResponseEntity<ErrorsResponse> {
        return ResponseEntity(ErrorsResponse(exception.error), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArguentNotValidException(
        req: HttpServletRequest,
        exception: MethodArgumentNotValidException
    ): ResponseEntity<ErrorsResponse> {
        val response = ErrorsResponse(exception.bindingResult.allErrors.map {
            ErrorMessage(
                code = it.code.toString().lowercase(),
                description = it.defaultMessage ?: ""
            )
        })
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(DateTimeParseException::class)
    fun dateTimeParseException(
        req: HttpServletRequest,
        exception: DateTimeParseException
    ): ResponseEntity<ErrorsResponse> {
        return ResponseEntity.badRequest().body(ErrorsResponse(DATE_TIME_INVALID_FORMAT))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgumentException(
        req: HttpServletRequest,
        exception: IllegalArgumentException
    ): ResponseEntity<ErrorsResponse> {
        return ResponseEntity.badRequest().body(ErrorsResponse(INVALID_FORMAT))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun httpMessageNotReadableException(
        req: HttpServletRequest,
        exception: HttpMessageNotReadableException
    ): ResponseEntity<ErrorsResponse> {
        val cause = exception.cause
        if (cause is InvalidFormatException) {
            val parentCause = cause.cause
            if(parentCause is DateTimeParseException) return dateTimeParseException(req, parentCause)

            val body = cause
                .path
                .mapNotNull { it.fieldName }
                .map {
                    ErrorMessage(
                        INVALID_FORMAT.code,
                        INVALID_FORMAT.description.replace("{0}", it)
                    )
                }

            return ResponseEntity.badRequest().body(ErrorsResponse(body))
        } else if(cause is IllegalArgumentException) {
            return ResponseEntity.badRequest().body(ErrorsResponse(INVALID_FORMAT))
        }

        return ResponseEntity.badRequest().build()
    }

    /*@ExceptionHandler(DataIntegrityViolationException::class)
    fun dataIntegrityViolationException(
        req: HttpServletRequest,
        exception: DataIntegrityViolationException
    ): ResponseEntity<ErrorsResponse> {
        return ResponseEntity.badRequest().body(ErrorsResponse(APIErrorEnum.DATE_TIME_INVALID_FORMAT))
    }*/

    //TODO: Criar um handler para DataIntegrityViolationException para quando receber uma restrição do banco de dados

}