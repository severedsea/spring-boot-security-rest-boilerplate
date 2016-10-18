package com.app.core.security

import groovy.transform.CompileStatic
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * REST authentication entry point implementation
 *
 * @implements {@link AuthenticationEntryPoint}
 * @author Hans Christian Ang
 * @version 0.1
 * @see {@link SecurityConfig}
 */
@CompileStatic
@Component
class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Authentication token was either missing or invalid.")
    }
}
