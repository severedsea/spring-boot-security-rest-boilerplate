package com.swagalls.auth

import com.swagalls.core.exception.CheckedException
import com.swagalls.core.security.CustomUserDetails
import com.swagalls.user.User
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.codec.Base64
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Authentication service layer
 *
 * Handles authentication-related logic
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
@Transactional
@Service
class AuthenticationService {

    @Autowired
    AuthenticationManager authenticationManager
    @Autowired
    SessionTokenRepository sessionTokenRepository

    /**
     * Validate username and password credentials
     *
     * @param username
     * @param password
     * @return Authentication - null if not authenticated
     */
    Authentication authenticate(String username, String password) {
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
        try {
            auth = authenticationManager.authenticate(auth)
            User user = ((CustomUserDetails) auth?.getPrincipal())?.user
            if(!user) {
                throw CheckedException.AUTHENTICATION_CREDENTIALS
            }
        }
        catch(AuthenticationException e) {
            throw CheckedException.AUTHENTICATION_CREDENTIALS
        }
        return auth
    }
    /**
     * Authenticate user based on HTTP header values
     *
     * @param authRequest
     * @return UserSession
     */
    SessionToken logIn(AuthenticationRequest authRequest) {
        authRequest = decodeBasicAuth(authRequest)

        Authentication auth = authenticate(authRequest.username, authRequest.password)
        User user = ((CustomUserDetails) auth.getPrincipal())?.user
        SecurityContextHolder.getContext().setAuthentication(auth)
        return createSession(user, authRequest)
    }

    /**
     * Decode Basic Access Authentication and populate username and password
     *
     * @param authRequest
     * @return AuthenticationRequest
     */
    private AuthenticationRequest decodeBasicAuth(AuthenticationRequest authRequest) {
        if(authRequest.authorization) {
            StringTokenizer tokenizer = new StringTokenizer(authRequest.authorization)
            // Validate if authorization value
            if (tokenizer.countTokens() < 2 || !tokenizer.nextToken().equalsIgnoreCase("Basic")) {
                throw CheckedException.AUTHENTICATION_TOKEN
            }

            // Decode Base64 value to get credentials
            String base64 = tokenizer.nextToken()
            String credentials = new String(Base64.decode(base64.getBytes(StandardCharsets.UTF_8)));

            // Parse username and password
            tokenizer = new StringTokenizer(credentials, ":")
            authRequest.username = tokenizer.nextToken()
            authRequest.password = tokenizer.nextToken()
        }

        if(!authRequest.username || !authRequest.password) {
            throw CheckedException.AUTHENTICATION_CREDENTIALS
        }
        return authRequest
    }

    /**
     * Create or update user session object
     *
     * @param user
     * @param authRequest
     * @return UserSession
     */
    private SessionToken createSession(User user, AuthenticationRequest authRequest) {
        SessionToken sessionToken = sessionTokenRepository.findByUser(user.id)
        if(!sessionToken) {
            sessionToken = new SessionToken(user, generateToken(user, authRequest))
            sessionTokenRepository.save(sessionToken)
        }
        return sessionToken
    }

    /**
     * Generate token based on user and authentication request values
     *
     * @param user
     * @param authRequest
     * @return String
     */
    private String generateToken(User user, AuthenticationRequest authRequest) {
        // Construct raw token
        String raw = user.id + "-" + authRequest.username + "-" + "-" + System.currentTimeMillis()

        // Hash raw token
        MessageDigest md = MessageDigest.getInstance("SHA-256")
        byte[] hash = md.digest(raw.getBytes("UTF-8"))

        // Convert hash to string
        String token = String.format("%064x", new BigInteger(1, hash))
        return token
    }

}
