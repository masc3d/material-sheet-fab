package org.deku.leo2.messaging.activemq;

import org.apache.activemq.transport.TransportAcceptListener;
import org.apache.activemq.transport.http.HttpTransportFactory;
import org.apache.activemq.transport.http.HttpTunnelServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import java.net.URI;
import java.util.HashMap;

//@Component("JMSTunnel")

/**
 * External http tunnel servlet for activemq.
 * Automatically starts the broker when initialized.
 */
public class HttpExternalTunnelServlet extends HttpTunnelServlet
        /*implements org.springframework.web.HttpRequestHandler**/ {
    private Log mLog = LogFactory.getLog(HttpExternalTunnelServlet.class);

    private HttpExternalTransportServer transportConnector;
    /** Public URI of jms servlet */
    private URI mPublicUri;
    /** Broker event listener */
    private BrokerEventListener mBrokerEventListener = new BrokerEventListener();

    /**
     * c'tor
     * @param publicUri The public URI under which the servlet will be reachable,
     *                  used for activemq discovery
     */
    public HttpExternalTunnelServlet(URI publicUri) {
        mPublicUri = publicUri;
        ContextImpl.instance().getBroker().getListenerEventDispatcher().add(mBrokerEventListener);
    }

    /** Broker event listener */
    private class BrokerEventListener implements BrokerImpl.Listener {

        @Override
        public void onStart() {
            mLog.info("Finalizing activemq external tunnel servlet initialization");

            // Listener is only avaialble once activemq broker has started
            TransportAcceptListener listener = transportConnector.getAcceptListener();
            getServletContext().setAttribute("acceptListener", listener);
            getServletContext().setAttribute("transportFactory", new HttpTransportFactory());
            getServletContext().setAttribute("transportOptions", new HashMap());

            try {
                HttpExternalTunnelServlet.super.init();
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void init() throws ServletException {
        mLog.info("Initializing activemq external tunnel servlet");

        //String url = "http://localhost:8080/jms";

        try {
            transportConnector = new
                    HttpExternalTransportServer(
                    mPublicUri, getServletContext());

            // Broker should not be started at this time
            ContextImpl.instance().getBroker().addConnector(transportConnector);
            ContextImpl.instance().getBroker().start();
        } catch (Exception e) {
            throw new ServletException(e);
        }

    }

//    @Override
//    /**
//     * Spring request handler support
//     */
//    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        service(request, response);
//    }
}