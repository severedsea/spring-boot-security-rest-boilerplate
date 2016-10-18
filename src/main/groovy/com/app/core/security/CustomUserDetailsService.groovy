package com.app.core.security

import com.app.user.User
import com.app.user.UserRepository
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * User Details service implementation for Spring Security's {@link UserDetailsService}
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
@Service
class CustomUserDetailsService implements UserDetailsService{
    @Autowired
    UserRepository userRepository

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
        if(user) {
            return new CustomUserDetails(user)
        }
        else {
            throw new UsernameNotFoundException("")
        }
    }
}
