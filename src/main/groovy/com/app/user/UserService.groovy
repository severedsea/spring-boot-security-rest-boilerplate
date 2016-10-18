package com.app.user

import com.app.auth.AuthenticationService
import com.app.core.exception.CheckedException
import com.app.core.security.CustomUserDetails
import groovy.transform.CompileStatic
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * USer service layer
 *
 * Handles user-related logic
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
@Transactional
@Service
class UserService {

    @Autowired
    AuthenticationService authenticationService
    @Autowired
    UserRepository userRepository
    @Autowired
    PasswordEncoder passwordEncoder

    /**
     * Creates a new user
     *
     * @param cmd {@link User}
     * @return User
     */
    User create(User cmd) {
        // Get existing user
        User user = userRepository.findByUsername(cmd?.username)
        if(user) {
            throw CheckedException.USER_EXISTS
        }

        user = new User()
        BeanUtils.copyProperties(cmd, user)
        user.password = passwordEncoder.encode(user.password)

        return userRepository.save(user)
    }

    /**
     * Updates an existing user
     *
     * @param cmd {@link User}
     * @return User
     */
    User update(User cmd) {
        User user = userRepository.findOne(cmd?.id)
        if(!user) {
            throw CheckedException.USER_NOT_FOUND
        }

        BeanUtils.copyProperties(cmd, user, "username", "password")
        return userRepository.save(user)
    }

    /**
    * Change a user's password
    *
    * @param cmd {@link User}
    * @return User
    */
    User changePassword(User cmd) {
        // Authenticate user credentials
        authenticationService.authenticate(cmd.username, cmd.password)

        // Get existing user
        User user = cmd ? userRepository.findByUsername(cmd.username) : null
        if(!user) {
            throw CheckedException.USER_NOT_FOUND
        }

        // Encode password
        user.password = passwordEncoder.encode(cmd.newPassword)
        return userRepository.save(user)
    }

    /**
     * Returns current authenticated user
     *
     * @return User
     */
    User current() {
        return ((CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal())?.user
    }
}
