package com.crud.usersweb.exceptions.handlers

import com.crud.usersweb.exceptions.APIErrorEnum
import com.crud.usersweb.exceptions.APIErrorEnum.DATE_TIME_INVALID_FORMAT
import com.crud.usersweb.exceptions.APIErrorEnum.INVALID_FORMAT
import com.crud.usersweb.exceptions.ResourceNotFoundException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.format.DateTimeParseException
import java.util.*

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
class ExceptionHandler(
    private val messageSource: MessageSource
) {

//    @Bean
//    fun messageSource(): MessageSource {
//        val messageSource = ReloadableResourceBundleMessageSource()
//        messageSource.setDefaultLocale(Locale.)
//        return messageSource
//    }

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

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun httpMessageNotReadableException(
        req: HttpServletRequest,
        exception: HttpMessageNotReadableException
    ): ResponseEntity<ErrorsResponse> {

        if (exception.cause is InvalidFormatException) {
            val body = (exception.cause as InvalidFormatException)
                .path
                .mapNotNull { it.fieldName }
                .map {
                    ErrorMessage(
                        INVALID_FORMAT.code,
                        messageSource.getMessage(INVALID_FORMAT.description, arrayOf(it), Locale.getDefault())
                    )
                }

            return ResponseEntity.badRequest().body(ErrorsResponse(body))
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