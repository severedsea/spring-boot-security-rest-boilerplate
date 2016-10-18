package com.swagalls.core.security

import com.swagalls.user.User
import groovy.transform.CompileStatic
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * User Details implementation for Spring Security's {@link UserDetails}
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
class CustomUserDetails implements UserDetails {

    User user

    CustomUserDetails(User user) {
        this.user = user
    }

    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO
        return null
    }

    @Override
    String getPassword() {
        return user.password
    }

    @Override
    String getUsername() {
        return user.username
    }

    @Override
    boolean isAccountNonExpired() {
        //TODO
        return true
    }

    @Override
    boolean isAccountNonLocked() {
        //TODO
        return true
    }

    @Override
    boolean isCredentialsNonExpired() {
        //TODO
        return true
    }

    @Override
    boolean isEnabled() {
        //TODO
        return true
    }
}
