package com.crud.usersweb.exceptions.handlers

import com.crud.usersweb.exceptions.APIErrorEnum
import com.crud.usersweb.exceptions.ResourceNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.format.DateTimeParseException

data class ErrorsResponse(
    var errorMessages: List<ErrorMessage>
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
                code = it.code.toString(),
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
        return ResponseEntity.badRequest().body(ErrorsResponse(APIErrorEnum.DATE_TIME_INVALID_FORMAT))
    }

}