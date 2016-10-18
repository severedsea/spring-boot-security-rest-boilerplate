package com.swagalls.core.web

import com.swagalls.core.exception.CheckedException
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Generic Error Controller
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
@ControllerAdvice
class ErrorHandler {
    @Autowired
    MessageHelper messageHelper

    /**
     * Handle {@link CheckedException}
     *
     * @param e - CheckedException
     * @return ErrorResponse
     */
    @ExceptionHandler(CheckedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleChecked(CheckedException e) {
        ErrorResponse response = new ErrorResponse(e.code, messageHelper.get(e.key, e.args))
        return response
    }

    /**
     * Handle {@link javax.validation.Valid} {@link com.swagalls.core.entity.AbstractEntity} validation exceptions
     *
     * @param e - MethodArgumentNotValidException
     * @return ErrorResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleCommandValidation(MethodArgumentNotValidException e) {
        ErrorResponse response = new ErrorResponse("9999", messageHelper.get("exception.validation"))

        Locale locale =  LocaleContextHolder.getLocale()
        List<FieldError> fieldErrors = e.getBindingResult()?.getFieldErrors()
        fieldErrors.each {
            response.details.add(it.field + " " + messageHelper.messageSource.getMessage(it, locale))
        }
        return response
    }
}