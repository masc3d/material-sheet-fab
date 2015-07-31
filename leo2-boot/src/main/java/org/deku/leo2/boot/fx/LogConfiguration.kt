package org.deku.leo2.boot.fx

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory

/**
 * Created by n3 on 01-Aug-15.
 */
object LogConfiguration {
    var rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
    var context = LoggerFactory.getILoggerFactory() as LoggerContext


}