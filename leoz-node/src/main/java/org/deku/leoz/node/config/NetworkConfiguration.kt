package org.deku.leoz.node.config

import org.apache.commons.lang3.SystemUtils
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import javax.annotation.PostConstruct

@Configuration
@Lazy(false)
open class NetworkConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    open fun onInitialize() {
        // Disable ipv6 on windows as it breaks mina-sshd
        // https://issues.apache.org/jira/browse/SSHD-786
        if (SystemUtils.IS_OS_WINDOWS) {
            log.warn("Disabling ipv6 on windows in order to mitigate https://issues.apache.org/jira/browse/SSHD-786")
            System.setProperty("java.net.preferIPv4Stack", "true")
        }
    }
}