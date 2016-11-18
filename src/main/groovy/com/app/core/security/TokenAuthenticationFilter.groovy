package com.app.core.security

import com.app.auth.SessionToken
import com.app.auth.SessionTokenRepository
import com.app.core.exception.CheckedException
import com.app.core.web.ErrorResponse
import com.app.core.web.MessageHelper
import com.app.user.User
import com.app.user.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Token-based Authentication Filter implementation
 *
 * Evaluates token information HTTP request header and handles authentication
 *
 * @extends {@link AbstractAuthenticationProcessingFilter}
 * @author Hans Christian Ang
 * @version 0.1
 * @see {@link SecurityConfig}
 */
@CompileStatic
@Component
class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String HEADER_TOKEN = "X-Auth-Token"

    @Autowired
    SessionTokenRepository sessionTokenRepository
    @Autowired
    UserRepository userRepository
    @Autowired
    MessageHelper messageHelper

    TokenAuthenticationFilter() {
        super("/**")

        // Provide empty AuthenticationSuccessHandler.onAuthenticationSuccess implementation
        setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                // Do nothing
            }
        })

        // Implements AuthenticationFailureHandler.onAuthenticationFailure
        setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler() {
            @Override
            void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
                // Handle CORS since it's not being handled by the CorsFilter in {@link SecurityConfig}
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*")
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*")
                response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600")

                // Write 401 error response
                response.setContentType("application/json")
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                new ObjectMapper().writeValue(response.getOutputStream(), new ErrorResponse(CheckedException.AUTHENTICATION_TOKEN.code, messageHelper.get(exception.message)))
            }
        })
    }

    @Override
    boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return true
    }

    @Override
    Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // Get auth token from request header
        String token = request.getHeader(HEADER_TOKEN)
        if(!token) {
            throw new AuthenticationServiceException("exception.authentication.token.null")
        }

        // Check if token exists in repo
        SessionToken sessionToken = sessionTokenRepository.findOne(token)
        if(!sessionToken) {
            throw new AuthenticationServiceException("exception.authentication.token.invalid")
        }
        User user = userRepository.findOne(sessionToken.user)

        // TODO: Roles integration
        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication auth = new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), null, authorities)
        return auth
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);

        // As this authentication is in HTTP header, after success we need to continue the request normally
        // and return the response as if the resource was not secured at all
        chain.doFilter(request, response);
    }

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager)
    }
}