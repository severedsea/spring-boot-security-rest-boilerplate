package com.swagalls.auth

import groovy.transform.CompileStatic
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * {@link SessionToken} repository
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
@Repository
interface SessionTokenRepository extends CrudRepository<SessionToken, String>  {
    SessionToken findByUser(Long userId)
}
