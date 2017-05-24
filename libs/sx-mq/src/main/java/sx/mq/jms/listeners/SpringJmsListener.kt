package sx.mq.jms.listeners

import org.springframework.jms.connection.JmsTransactionManager
import org.springframework.jms.listener.DefaultMessageListenerContainer
import org.springframework.jms.listener.SessionAwareMessageListener
import org.springframework.util.ErrorHandler
import sx.mq.jms.JmsEndpoint
import sx.mq.jms.JmsListener
import java.util.concurrent.Executor
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session

/**
 * Spring listener implementation
 * Created by masc on 07.06.15.
 * @param endpoint The channel to listen on
 * @param durationClientId The client id to use for durable topic subscriptions. Setting this identifier enables durable subscription.
 * @param executor The executor to spawn threads from
 */
open class SpringJmsListener(
        endpoint: JmsEndpoint,
        private val executor: Executor,
        private val durableClientId: String? = null)
    :
        JmsListener(endpoint),
        ErrorHandler {

    /** Spring message listener container  */
    private var listenerContainer: DefaultMessageListenerContainer? = null
    /** Transaction manager  */
    private val transactionManager: JmsTransactionManager? = null

    /**
     * Optionally overridden to customize listener container configuration
     * @param listenerContainer
     */
    open protected fun configure(listenerContainer: DefaultMessageListenerContainer) {
    }

    private val description by lazy({
        "[${this.javaClass.simpleName}] for [${this.endpoint}]"
    })

    /**
     * Start listener
     */
    @Synchronized final override fun start() {
        log.info("Starting ${this.description}")
        var lc = listenerContainer
        if (lc == null) {
            lc = DefaultMessageListenerContainer()

            if (this.durableClientId != null) {
                lc.clientId = this.durableClientId
                lc.isSubscriptionDurable = true
            }
            lc.connectionFactory = this.context.connectionFactory
            lc.messageListener = object : SessionAwareMessageListener<Message> {
                @Throws(JMSException::class)
                override fun onMessage(message: Message, session: Session) {
                    this@SpringJmsListener.onMessage(message, session)
                }
            }
            lc.isSessionTransacted = true
            lc.errorHandler = this
            lc.destination = this.endpoint.destination
            lc.setTaskExecutor(this.executor)
            this.configure(lc)
            lc.afterPropertiesSet()
            this.listenerContainer = lc
        }
        lc.start()
    }

    /**
     * Spring error handler
     * @param t Error
     */
    final override fun handleError(t: Throwable) {
        this.onError(t)
    }

    /**
     * Stop listener
     */
    @Synchronized override fun stop() {
        val lc = listenerContainer
        if (lc != null) {
            log.info("Stopping ${this.description}")
            lc.shutdown()
            listenerContainer = null
        }
    }
}
