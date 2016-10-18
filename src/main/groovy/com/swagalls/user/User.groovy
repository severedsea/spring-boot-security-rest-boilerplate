package com.swagalls.user

import com.fasterxml.jackson.annotation.JsonView
import com.swagalls.core.entity.DomainEntity
import groovy.transform.CompileStatic

import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * User Entity
 *
 * @author  Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
@Entity
class User extends DomainEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @NotNull(groups=[Update])
    @JsonView(Default)
    Long id

    @NotNull(groups=[Create, ChangePassword])
    @Size(min = 1, max = 30)
    @JsonView(Default)
    String username

    @NotNull(groups=[Create, ChangePassword])
    @Size(min = 1, max = 255)
    String password

    @Transient
    @NotNull(groups=[ChangePassword])
    @Size(min = 1, max = 255, groups=[ChangePassword])
    String newPassword

    User(){}

    /////// JSON Views and Validation Groups classes
    interface Default {}

    interface Create {}
    interface Update {}
    interface ChangePassword {}
}
