package sx.jms.listeners

import sx.jms.Channel
import sx.jms.Listener
import java.util.concurrent.Executor
import javax.jms.*

/**
 * Simple jms listener
 * Created by masc on 17.04.15.
 * @param connectionFactory Connection factory
 * @param converter Message converter
 */
abstract class SimpleListener
:
        Listener
{
    constructor (channel: () -> Channel, executor: Executor) : super(channel, executor)

    protected val connection: Connection by lazy {
        this.channel.connectionFactory!!.createConnection()
    }

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
                    this@SimpleListener.executor.execute {
                        var transacted = session.transacted
                        try {
                            this@SimpleListener.onMessage(message, session)
                            if (transacted)
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
