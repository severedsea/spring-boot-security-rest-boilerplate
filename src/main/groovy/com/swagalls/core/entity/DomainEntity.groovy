package com.swagalls.core.entity

import groovy.transform.CompileStatic

import javax.persistence.*

/**
 * Domain entity object
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
@MappedSuperclass
abstract class DomainEntity {
    @Column(insertable=true, updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    Date created

    @Column(insertable=false, updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    Date updated

    @PrePersist
    void beforePersist() {
        this.created = new Date()
    }

    @PreUpdate
    void beforeUpdate() {
        this.updated = new Date()
    }
}
