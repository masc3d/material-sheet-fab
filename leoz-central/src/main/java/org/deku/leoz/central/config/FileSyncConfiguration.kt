package org.deku.leoz.central.config

import org.deku.leoz.Identity
import org.deku.leoz.central.services.AuthorizationService
import org.deku.leoz.central.services.FileSyncService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Created by masc on 17/03/16.
 */
@Configuration
@Lazy(false)
open class FileSyncConfiguration {

    @Inject
    private lateinit var authorizationService: AuthorizationService

    @Bean
    open fun fileSyncService(): FileSyncService {
        return FileSyncService()
    }
    private val fileSyncService by lazy { fileSyncService() }

    @PostConstruct
    fun onInitialize() {

        this.authorizationService.delegate.add(object : AuthorizationService.Listener {
            override fun onAuthorized(nodeIdentityKey: Identity.Key) {
                fileSyncService.prepareForNode(nodeIdentityKey)
            }
        })
    }
}