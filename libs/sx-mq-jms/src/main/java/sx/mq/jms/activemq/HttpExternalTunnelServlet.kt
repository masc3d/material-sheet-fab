package sx.mq.jms.activemq

import org.apache.activemq.transport.http.HttpTransportFactory
import org.apache.activemq.transport.http.HttpTunnelServlet
import org.slf4j.LoggerFactory
import sx.mq.MqBroker
import java.net.URI
import java.util.*
import java.util.concurrent.Executors
import javax.servlet.ServletException

/**
 * External http tunnel servlet for activemq.
 * Automatically starts the broker when initialized.
 * @param publicUri The public URI under which the servlet will be reachable,
 *                  used for activemq discovery
 */
class HttpExternalTunnelServlet(
        /** Public URI of jms servlet  */
        private val publicUri: URI) : HttpTunnelServlet() {
    private val log = LoggerFactory.getLogger(HttpExternalTunnelServlet::class.java)

    private var transportConnector: HttpExternalTransportServer? = null
    /** Broker event listener  */
    private val brokerEventListener = BrokerEventListener()

    init {
        ActiveMQBroker.instance.delegate.add(brokerEventListener)
    }

    /** Broker event listener  */
    private inner class BrokerEventListener : MqBroker.DefaultEventListener() {

        override fun onStart() {
            log.info("Finalizing activemq external tunnel servlet initialization")

            // Listener is only avaialble once activemq broker has started
            val listener = transportConnector!!.acceptListener
            servletContext.setAttribute("acceptListener", listener)
            servletContext.setAttribute("transportFactory", HttpTransportFactory())
            servletContext.setAttribute("transportOptions", HashMap<Any, Any>())

            try {
                super@HttpExternalTunnelServlet.init()
            } catch (e: ServletException) {
                throw RuntimeException(e)
            }
        }
    }

    @Throws(ServletException::class)
    override fun init() {
        log.info("Initializing activemq external tunnel servlet")

        //String url = "http://localhost:8080/jms";

        try {
            val transportConnector = HttpExternalTransportServer(
                    publicUri, servletContext)

            // Broker should not be started at this time
            ActiveMQBroker.instance.addConnector(transportConnector)

            this.transportConnector = transportConnector

            // Start broker threaded to improve startup time
            val exec = Executors.newSingleThreadExecutor()
            exec.execute {
                ActiveMQBroker.instance.start()
            }
        } catch (e: Exception) {
            throw ServletException(e)
        }
    }
}