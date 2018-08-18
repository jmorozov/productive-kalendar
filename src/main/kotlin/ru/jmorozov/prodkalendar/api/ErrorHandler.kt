package ru.jmorozov.prodkalendar.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.servlet.http.HttpServletRequest
import javax.validation.ValidationException

@RestControllerAdvice
class ErrorHandler: ResponseEntityExceptionHandler() {

    companion object {
        val log: Logger = LoggerFactory.getLogger(ErrorHandler::class.java.name)
    }

    override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException,
                                              headers: HttpHeaders,
                                              status: HttpStatus,
                                              request: WebRequest): ResponseEntity<Any> {
        return ResponseEntity(ex.localizedMessage, headers, status)
    }

    @ExceptionHandler(ValidationException::class)
    fun processBusinessRuleException(ex: ValidationException): ResponseEntity<String> {
        log.info("Incorrect request", ex)

        return ResponseEntity("Incorrect request", HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun processException(req: HttpServletRequest, ex: Exception): ResponseEntity<String> {
        log.error("Exception occurred while processing request", ex)

        return ResponseEntity("Internal error", HttpStatus.INTERNAL_SERVER_ERROR)
    }

}