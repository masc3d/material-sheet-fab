package sx.jms.listeners

import sx.LazyInstance
import sx.jms.Channel
import sx.jms.Listener
import javax.jms.*

/**
 * Simple single threaded jms listener
 * Created by masc on 17.04.15.
 * @param connectionFactory Connection factory
 * @param converter Message converter
 */
open class SimpleListener(channel: () -> Channel)
    :
        Listener(channel) {

    private val connection = LazyInstance<Connection>({
        this.channel.connectionFactory!!.createConnection()
    })

    private val session = LazyInstance<Session>({
        this.connection.get().createSession(true, Session.AUTO_ACKNOWLEDGE)
    })

    private val consumer = LazyInstance<MessageConsumer>({
        this.session.get().createConsumer(this.channel.destination)
    })

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
                val session = this@SimpleListener.session.get()
                try {
                    this@SimpleListener.onMessage(message, session)
                    if (session.transacted)
                        session.commit()
                } catch (e: Exception) {
                    // TODO: verify if exception is routed to onException handler when not caught here
                    log.error(e.message, e)

                    if (session.transacted) {
                        try {
                            session.rollback()
                        } catch (e1: JMSException) {
                            log.error(e.message, e)
                        }
                    }
                }
            }
        }

        this.connection.get().start()
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
    }
}
