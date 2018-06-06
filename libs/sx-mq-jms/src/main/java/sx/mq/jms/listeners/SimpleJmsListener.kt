package sx.mq.jms.listeners

import sx.LazyInstance
import sx.mq.jms.JmsEndpoint
import sx.mq.jms.JmsListener
import javax.jms.*

/**
 * Simple single threaded jms listener
 * Created by masc on 17.04.15.
 * @param connectionFactory Connection factory
 * @param converter Message converter
 */
open class SimpleJmsListener(endpoint: JmsEndpoint)
    :
        JmsListener(endpoint) {

    private val connection = LazyInstance<Connection>({
        this.context.connectionFactory.createConnection()
    })

    private val session = LazyInstance<Session>({
        this.connection.get().createSession(true, Session.AUTO_ACKNOWLEDGE)
    })

    private val consumer = LazyInstance<MessageConsumer>({
        this.session.get().createConsumer(this.endpoint.destination)
    })

    override var isRunning: Boolean = false

    /**
     * Start listener
     * @throws JMSException
     */
    @Throws(JMSException::class)
    @Synchronized final override fun start() {
        this.stop()

        // Wrap message callback, adding jms transaction support
        this.consumer.get().messageListener = object : MessageListener {
            override fun onMessage(message: Message) {
                val session = this@SimpleJmsListener.session.get()
                try {
                    this@SimpleJmsListener.onMessage(message, session)
                    if (session.transacted)
                        session.commit()
                } catch (e: Throwable) {
                    if (session.transacted) {
                        try {
                            session.rollback()
                        } catch (e: JMSException) {
                            log.error(e.message, e)
                        }
                    }

                    this@SimpleJmsListener.onError(e)
                }
            }
        }

        this.connection.get().start()

        this.isRunning = true
    }

    /**
     * Stop listener
     * @throws JMSException
     */
    @Throws(JMSException::class)
    @Synchronized final override fun stop() {
        this.consumer.ifSet {
            it.close()
        }

        this.session.ifSet {
            it.close()
        }

        this.connection.ifSet {
            it.close()
        }

        this.isRunning = false
    }
}
