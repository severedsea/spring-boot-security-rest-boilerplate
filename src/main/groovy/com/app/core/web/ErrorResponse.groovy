package com.app.core.web

import groovy.transform.CompileStatic

/**
 * Error Response wrapper
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
class ErrorResponse {
    String code
    String message
    List<String> details

    ErrorResponse() {}

    ErrorResponse(String code, String message) {
        this.code = code
        this.message = message
        this.details = new ArrayList<String>()
    }
}
