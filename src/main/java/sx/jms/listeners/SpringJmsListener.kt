package sx.jms.listeners

import org.springframework.jms.connection.JmsTransactionManager
import org.springframework.jms.listener.DefaultMessageListenerContainer
import org.springframework.jms.listener.SessionAwareMessageListener
import org.springframework.util.ErrorHandler
import sx.jms.Channel
import sx.jms.Listener
import java.util.concurrent.Executor
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session

/**
 * Spring listener implementation
 * Created by masc on 07.06.15.
 * @param connectionFactory
 * @property destination
 * @param converter Message converter
 */
abstract class SpringJmsListener
:
        Listener,
        ErrorHandler {

    constructor (channel: () -> Channel, executor: Executor) : super(channel, executor)

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
        "[${this.javaClass.simpleName}] for [${this.channel}]"
    })

    /**
     * Start listener
     */
    @Synchronized final override fun start() {
        log.info("Starting jms listener ${this.description}")
        var lc = listenerContainer
        if (lc == null) {
            lc = DefaultMessageListenerContainer()

            lc.connectionFactory = this.channel.connectionFactory
            lc.messageListener = object : SessionAwareMessageListener<Message> {
                @Throws(JMSException::class)
                override fun onMessage(message: Message, session: Session) {
                    this@SpringJmsListener.onMessage(message, session)
                }
            }
            lc.isSessionTransacted = true
            lc.errorHandler = this
            lc.destination = this.channel.destination
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
