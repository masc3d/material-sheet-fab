package org.deku.leoz.central.config

import org.deku.leoz.central.Application
import org.deku.leoz.central.service.internal.filesync.FileSyncHostService
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.node.Storage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.util.concurrent.ScheduledExecutorService
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Created by masc on 17/03/16.
 */
@Configuration
@Lazy(false)
class FileSyncHostConfiguration {

    @Inject
    private lateinit var application: Application
    @Inject
    private lateinit var storage: Storage
    @Inject
    private lateinit var messageListenerConfiguration: MqListenerConfiguration
    @Inject
    private lateinit var executorService: ScheduledExecutorService

    /**
     * File sync service
     */
    @get:Bean
    val fileSyncService: FileSyncHostService
        get() = FileSyncHostService(
                baseDirectory = storage.transferDirectory,
                executorService = this.executorService,
                identity = this.application.identity,
                nodeEndpointSupplier = { JmsEndpoints.node.queue(it) }
        )

    /**
     * Initialize
     */
    @PostConstruct
    fun onInitialize() {
        messageListenerConfiguration.centralMainListener.addDelegate(
                this.fileSyncService)

        this.fileSyncService.start()
    }

    @PreDestroy
    fun onDestroy() {
        this.fileSyncService.close()
    }
}