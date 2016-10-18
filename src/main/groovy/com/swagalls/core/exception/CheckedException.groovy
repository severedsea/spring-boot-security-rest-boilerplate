package com.swagalls.core.exception

import groovy.transform.CompileStatic

/**
 * Checked Exception object
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
class CheckedException extends RuntimeException {
    String code
    List<Object> args = new ArrayList<>()
    String key

    CheckedException(String code, String key) {
        this.code = code
        this.key = key
    }

    // Core exceptions
    static CheckedException UNKNOWN = new CheckedException("0000", "exception.unknown")

    // Authentication exceptions
    static CheckedException AUTHENTICATION_CREDENTIALS = new CheckedException("1000", "exception.authentication.credentials")
    static CheckedException AUTHENTICATION_TOKEN = new CheckedException("1001", "exception.authentication.token")

    // User exceptions
    static CheckedException USER_EXISTS = new CheckedException("2000", "exception.user.exists")
    static CheckedException USER_NOT_FOUND = new CheckedException("2001", "exception.user.not.found")

    // TODO: Remove
    static CheckedException TEST_EXCEPTION = new CheckedException("9998", "exception.test")

    /**
     * Helper method to throw exception with message arguments
     * @param ce
     * @param args
     * @return CheckedException
     */
    CheckedException addArgs(Object... args) {
        args.each { this.args.add(it) }
        return this
    }
}
