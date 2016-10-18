package com.app.core.web

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

/**
 * {@link MessageSource} helper
 *
 * @author Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
@Component
class MessageHelper {
    @Autowired
    MessageSource messageSource

    /**
     * Get message from resource bundle based on message key
     *
     * @param key
     * @return String
     */
    String get(String key) {
        return get(key, null)
    }

    /**
     * Get message from resource bundle based on message key and args
     *
     * @param key
     * @param args
     * @return String
     */
    String get(String key, List<Object> args) {
        String message = key
        try {
            message = messageSource.getMessage(key, args?.toArray(), LocaleContextHolder.getLocale())
        }
        catch (NoSuchMessageException ne) { /* Do nothing */  }
        return message
    }
}
