package com.swagalls.core.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.swagalls.auth.SessionToken
import com.swagalls.auth.SessionTokenRepository
import com.swagalls.core.exception.CheckedException
import com.swagalls.core.web.ErrorResponse
import com.swagalls.core.web.MessageHelper
import com.swagalls.user.User
import com.swagalls.user.UserRepository
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
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
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
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
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // Implements AuthenticationSuccessHandler.onAuthenticationSuccess
        setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                // Override to continue with the filter chain since default handling is URL redirection
                chain.doFilter(request, response)
            }
        })

        // Implements AuthenticationFailureHandler.onAuthenticationFailure
        setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler() {
            @Override
            void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
                response.setContentType("application/json")
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                new ObjectMapper().writeValue(response.getOutputStream(), new ErrorResponse(CheckedException.AUTHENTICATION_TOKEN.code, messageHelper.get(exception.message)))
            }
        })

        super.doFilter(req, res, chain)
    }

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager)
    }
}