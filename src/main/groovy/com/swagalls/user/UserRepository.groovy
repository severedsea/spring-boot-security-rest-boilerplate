package com.swagalls.user

import groovy.transform.CompileStatic
import org.springframework.data.repository.CrudRepository

/**
 * User Domain - Spring Data JPA Repository
 *
 * @author  Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username)
}
