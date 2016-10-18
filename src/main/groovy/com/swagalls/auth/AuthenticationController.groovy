package com.swagalls.auth

import com.fasterxml.jackson.annotation.JsonView
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest

/**
 * Authentication REST Controller
 *
 * Handles HTTP requests for authentication-related API methods
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
@RestController
@RequestMapping(value="/auth", produces="application/json")
class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService

    @RequestMapping("/login")
    @JsonView(SessionToken.Default)
    SessionToken login(HttpServletRequest request) {
        return authenticationService.logIn(new AuthenticationRequest(request))
    }
}
