package org.deku.leoz.node.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import org.deku.leoz.node.Application
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.test.context.ActiveProfiles
import javax.annotation.PostConstruct

/**
 * Application test configuration
 * Created by masc on 12/03/2017.
 */
@Configuration
@EnableConfigurationProperties
@ActiveProfiles(Application.PROFILE_NODE)
@Lazy(false)
class ApplicationTestConfiguration : ApplicationConfiguration() {

    @PostConstruct
    fun onInitialize() {
        Kodein.global.addImport(module)
        Kodein.global.addImport(LogConfiguration.module)
    }
}