package sx.jms.listeners

import sx.jms.Converter
import sx.jms.Listener

import javax.jms.*

/**
 * Simple single threaded jms listener
 * Created by masc on 17.04.15.
 * @param connectionFactory Connection factory
 * @param converter Message converter
 */
abstract class SimpleListener(connectionFactory: ConnectionFactory, converter: Converter? = null)
:
        Listener(connectionFactory, converter) {
    protected val connection: Connection by lazy({
        this.connectionFactory.createConnection()
    })

    /**
     * Override to create customized session
     * @param connection
     * @throws JMSException
     * @return JMS session
     */
    @Throws(JMSException::class)
    protected abstract fun createSession(connection: Connection): Session

    /**
     * Override to create destinations to listen on
     * @param session
     * *
     * @return JMS destinations
     * *
     * @throws JMSException
     */
    @Throws(JMSException::class)
    protected abstract fun createDestinations(session: Session): List<Destination>

    /**
     * Start listener
     * @throws JMSException
     */
    @Throws(JMSException::class)
    final override fun start() {
        this.stop()

        this.connection.start()
        val session = this.createSession(connection)

        val destinations = this.createDestinations(session)
        for (d in destinations) {
            val mc = session.createConsumer(d)

            // Wrap message callback, adding jms transaction support
            mc.messageListener = object : MessageListener {
                override fun onMessage(message: Message) {
                    var transacted = false
                    try {
                        this@SimpleListener.onMessage(message, session)
                        if (session.transacted)
                            session.commit()
                    } catch (e: Exception) {
                        // TODO: verify if exception is routed to onException handler when not caught here
                        log.error(e.message, e)

                        if (transacted) {
                            try {
                                session.rollback()
                            } catch (e1: JMSException) {
                                log.error(e.message, e)
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * Stop listener
     * @throws JMSException
     */
    @Throws(JMSException::class)
    final override fun stop() {
        this.connection.close()
    }
}
