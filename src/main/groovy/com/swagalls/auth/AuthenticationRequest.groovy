package com.swagalls.auth

import groovy.transform.CompileStatic
import org.springframework.http.HttpHeaders

import javax.servlet.http.HttpServletRequest

/**
 * Authentication Request wrapper
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
class AuthenticationRequest {
    String authorization
    String username
    String password
//    String ipAddress
//    String userAgent

    private final String KEY_USERNAME = "X-Username"
    private final String KEY_PASSWORD = "X-Password"
//    private final String KEY_IP_ADDR  = "X-Forwarded-For"

    AuthenticationRequest(HttpServletRequest request) {
        this.username = request.getHeader(KEY_USERNAME)
        this.password = request.getHeader(KEY_PASSWORD)
        this.authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
//        this.ipAddress = request.getHeader(KEY_IP_ADDR) ?: request.getRemoteAddr()
//        this.userAgent = request.getHeader(HttpHeaders.USER_AGENT)
    }
}
