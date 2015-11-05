package sx.jms.listeners

import org.springframework.jms.connection.JmsTransactionManager
import org.springframework.jms.listener.DefaultMessageListenerContainer
import org.springframework.jms.listener.SessionAwareMessageListener
import org.springframework.util.ErrorHandler
import sx.jms.Converter
import sx.jms.Listener
import javax.jms.*

/**
 * Spring listener implementation
 * Created by masc on 07.06.15.
 * @param connectionFactory
 * @property destination
 * @param converter Message converter
 */
abstract class SpringJmsListener(
        connectionFactory: ConnectionFactory,
        /** Destination this listener will be attached to  */
        val destination: () -> Destination,
        converter: Converter? = null)
:
        Listener(connectionFactory, converter),
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
        "[${this.javaClass.simpleName}] for [${this.destination().toString()}]"
    })

    /**
     * Start listener
     */
    @Synchronized override fun start() {
        log.info("Starting jms listener ${this.description}")
        var lc = listenerContainer
        if (lc == null) {
            lc = DefaultMessageListenerContainer()

            lc.connectionFactory = this.connectionFactory
            lc.messageListener = object : SessionAwareMessageListener<Message> {
                @Throws(JMSException::class)
                override fun onMessage(message: Message, session: Session) {
                    this@SpringJmsListener.onMessage(message, session)
                }
            }
            lc.isSessionTransacted = true
            lc.errorHandler = this
            lc.destination = this.destination()
            this.configure(lc)
            lc.afterPropertiesSet()
            this.listenerContainer = lc
        }
        lc.start()
    }

    /**
     * Stop listener
     */
    @Synchronized override fun stop() {
        val lc = listenerContainer
        if (lc != null) {
            log.info("Stopping %s".format(this.javaClass.simpleName))
            lc.shutdown()
            listenerContainer = null
        }
    }

    override fun handleError(t: Throwable) {
        log.error(t.message, t)
    }

    override fun dispose() {
        this.stop()
    }
}
