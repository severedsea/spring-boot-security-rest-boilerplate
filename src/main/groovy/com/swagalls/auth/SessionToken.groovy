package com.swagalls.auth

import com.fasterxml.jackson.annotation.JsonView
import com.swagalls.core.entity.AbstractEntity
import com.swagalls.user.User
import groovy.transform.CompileStatic

import javax.persistence.Entity
import javax.persistence.Id

/**
 * Session Token entity
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
@Entity
class SessionToken extends AbstractEntity {

    @Id
    @JsonView(Default)
    String token

    Long user

    SessionToken(){}

    SessionToken(User user) {
        this.user = user.id
    }

    SessionToken(User user, String token) {
        this(user)
        this.token = token
    }

    /////// JSON Views and Validation Groups classes
    interface Default {}
}
