package org.deku.leoz.central.config

import org.deku.leoz.central.App
import org.deku.leoz.central.services.FileSyncHostService
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.messaging.entities.FileSyncMessage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.Channel
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
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    /**
     * File sync service
     */
    @Bean
    open fun fileSyncService(): FileSyncHostService {
        return FileSyncHostService(
                baseDirectory = StorageConfiguration.instance.transferDirectory,
                executorService = this.executorService,
                identity = App.instance.identity,
                nodeChannelSupplier = { it -> Channel(ActiveMQConfiguration.instance.nodeQueue(it)) })
    }
    private val fileSyncService by lazy { fileSyncService() }

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