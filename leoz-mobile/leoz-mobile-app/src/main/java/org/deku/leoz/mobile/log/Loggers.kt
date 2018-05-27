package org.deku.leoz.mobile.log

import sx.log.slf4j.info

/**
 * Logger extensions
 * Created by masc on 26.02.18.
 */

/** Log user action */
fun org.slf4j.Logger.user(message: () -> Any) {
    this.info { "User : ${message()}" }
}