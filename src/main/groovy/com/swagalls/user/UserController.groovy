package com.swagalls.user

import com.fasterxml.jackson.annotation.JsonView
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * User REST Controller
 *
 * Handles HTTP requests for user-related API methods
 *
 * @author Hans Christian Ang
 * @version 0.1
 * @since 10/11/2016
 */
@CompileStatic
@RestController
@RequestMapping(value="/user", produces="application/json")
class UserController {

    @Autowired
    UserService userService

    @RequestMapping(value="/create")
    @JsonView(User.Default)
    User create(@Validated(User.Create) @RequestBody User cmd) {
        return userService.create(cmd)
    }

    @RequestMapping(value="/update")
    @JsonView(User.Default)
    User save(@Validated(User.Update) @RequestBody User cmd) {
        return userService.update(cmd)
    }

    @RequestMapping(value="/changePassword")
    @JsonView(User.Default)
    User changePassword(@Validated(User.ChangePassword) @RequestBody User cmd) {
        return userService.changePassword(cmd)
    }
}
