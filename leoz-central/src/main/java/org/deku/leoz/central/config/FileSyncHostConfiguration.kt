package org.deku.leoz.central.config

import org.deku.leoz.central.Application
import org.deku.leoz.central.service.internal.filesync.FileSyncHostService
import org.deku.leoz.config.JmsChannels
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
open class FileSyncHostConfiguration {

    @Inject
    private lateinit var application: Application
    @Inject
    private lateinit var storage: Storage
    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration
    @Inject
    private lateinit var executorService: ScheduledExecutorService

    /**
     * File sync service
     */
    @get:Bean
    open val fileSyncService: FileSyncHostService
        get() = FileSyncHostService(
                baseDirectory = storage.transferDirectory,
                executorService = this.executorService,
                identity = this.application.identity,
                nodeChannelSupplier = { JmsChannels.node.queue(it) }
        )

    /**
     * Initialize
     */
    @PostConstruct
    fun onInitialize() {
        messageListenerConfiguration.centralQueueListener.addDelegate(
                this.fileSyncService)

        this.fileSyncService.start()
    }

    @PreDestroy
    fun onDestroy() {
        this.fileSyncService.close()
    }
}