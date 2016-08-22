package org.deku.leoz.discovery

/**
 * Created by masc on 22/08/16.
 */
enum class ServiceType(val value: String) {
    HTTPS("https"),
    ACTIVEMQ_NATIVE("activemq_native"),
    RSYNC("rsync"),
    SSH("ssh")
}
