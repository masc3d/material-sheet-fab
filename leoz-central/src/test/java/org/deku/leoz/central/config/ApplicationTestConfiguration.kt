package org.deku.leoz.central.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import org.deku.leoz.node.Application
import org.deku.leoz.node.config.ApplicationConfiguration
import org.deku.leoz.node.config.LogConfiguration
import org.springframework.context.annotation.Lazy
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ActiveProfiles
import javax.annotation.PostConstruct

/**
 * Application test configuration
 * Created by masc on 12/03/2017.
 */
@Configuration
@EnableConfigurationProperties
@ActiveProfiles(Application.PROFILE_CLIENT_NODE)
@Lazy(false)
open class ApplicationTestConfiguration : ApplicationConfiguration() {

    init {
        Kodein.global.mutable = true
        Kodein.global.clear()
    }

    @PostConstruct
    open fun onInitialize() {
        Kodein.global.addImport(ApplicationConfiguration.module)
        Kodein.global.addImport(LogConfiguration.module)
    }
}