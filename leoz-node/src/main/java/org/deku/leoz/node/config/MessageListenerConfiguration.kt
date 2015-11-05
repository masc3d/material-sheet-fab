package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.messaging.AuthorizationMessageHandler
import org.deku.leoz.node.messaging.entities.AuthorizationMessage
import org.deku.leoz.update.entities.UpdateInfo
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import sx.jms.converters.DefaultConverter
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker
import sx.jms.listeners.SpringJmsListener
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Message listener configuration.
 * Initializes message listener(s) and their message handlers
 * Created by masc on 20.06.15.
 */
@Profile(App.PROFILE_CLIENT_NODE)
@Configuration
@Lazy(false)
open class MessageListenerConfiguration {
    private val log = LogFactory.getLog(MessageListenerConfiguration::class.java)

    @Inject
    lateinit private var identityConfiguration: IdentityConfiguration

    @Inject
    lateinit private var updaterConfiguration: UpdaterConfiguration

    private var nodeQueueListener: SpringJmsListener

    private var nodeNotificationListener: SpringJmsListener

    init {
        // Configure and create listeners
        nodeQueueListener = object : SpringJmsListener(
                connectionFactory = ActiveMQConfiguration.instance.broker.connectionFactory,
                destination = { ActiveMQConfiguration.instance.nodeQueue(identityConfiguration.identity.id!!) },
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP)
        ) {}

        // Configure and create listeners
        nodeNotificationListener = object : SpringJmsListener(
                connectionFactory = ActiveMQConfiguration.instance.broker.connectionFactory,
                destination = { ActiveMQConfiguration.instance.nodeNotificationTopic },
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP)
        ) {
        }
    }

    private fun initializeListener() {
        // Add message handler delegatess
        nodeQueueListener.addDelegate(
                AuthorizationMessage::class.java,
                AuthorizationMessageHandler()
        )

        nodeNotificationListener.addDelegate(
                UpdateInfo::class.java,
                updaterConfiguration.updater
        )
    }

    //region Lifecycle
    @PostConstruct
    fun onInitialize() {
        // Register event listeners
        ActiveMQBroker.instance().delegate.add(brokerEventListener)
        identityConfiguration.identity.delegate.add(identityEventListener)

        this.startIfReady()
    }

    @PreDestroy
    fun onDestroy() {
        this.nodeQueueListener.stop()
    }

    /**
     * Broker event listener
     */
    private var brokerEventListener: Broker.EventListener = object : Broker.EventListener {
        override fun onStart() {
            startIfReady()
        }

        override fun onStop() {
            stop()
        }
    }

    /**
     * Identity event listener
     */
    private var identityEventListener: Identity.Listener = object : Identity.Listener {
        override fun onIdUpdated(identity: Identity) {
            startIfReady()
        }
    }

    /**
     * Indicates if message listener is ready to start (prerequisites are met)
     * @return
     */
    private val isReadyToStart: Boolean
        get() = ActiveMQConfiguration.instance.broker.isStarted && identityConfiguration.identity.id != null

    /**
     * Start message listener
     */
    @Synchronized private fun startIfReady() {
        this.stop()

        if (this.isReadyToStart) {
            this.initializeListener()
            this.nodeQueueListener.start()
            this.nodeNotificationListener.start()
        }
    }

    /**
     * Stop message listener
     */
    @Synchronized private fun stop() {
        this.nodeQueueListener.stop()
        this.nodeNotificationListener.stop()
    }
    //endregion
}
