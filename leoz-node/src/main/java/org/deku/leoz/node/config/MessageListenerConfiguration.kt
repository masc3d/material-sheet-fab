package org.deku.leoz.node.config

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.messaging.activemq.ActiveMQContext
import org.deku.leoz.node.App
import org.deku.leoz.node.auth.Identity
import org.deku.leoz.node.messaging.MessageListener
import org.deku.leoz.node.messaging.auth.AuthorizationMessageHandler
import org.deku.leoz.node.messaging.auth.v1.AuthorizationMessage
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import kotlin.properties.Delegates

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

    private var messageListener: MessageListener by Delegates.notNull()

    @Inject
    lateinit private var identityConfiguration: IdentityConfiguration

    @PostConstruct
    fun onInitialize() {
        log.info("Initializing node message listener")

        // Configure and create listener
        messageListener = MessageListener(
                ActiveMQContext.instance,
                identityConfiguration.identity)

        // Register event listeners
        ActiveMQBroker.instance().delegate.add(brokerEventListener)
        identityConfiguration.identity.delegate.add(identityEventListener)

        this.startIfReady()
    }

    @PreDestroy
    fun onDestroy() {
        messageListener.stop()
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
        get() = ActiveMQContext.instance.broker.isStarted && identityConfiguration.identity.id != null

    /**
     * Start message listener
     */
    @Synchronized private fun startIfReady() {
        this.stop()

        if (this.isReadyToStart) {
            // Add message handler delegatess
            messageListener.addDelegate(AuthorizationMessage::class.java, AuthorizationMessageHandler())
            messageListener.start()
        }
    }

    /**
     * Stop message listener
     */
    @Synchronized private fun stop() {
        messageListener.stop()
    }
}
