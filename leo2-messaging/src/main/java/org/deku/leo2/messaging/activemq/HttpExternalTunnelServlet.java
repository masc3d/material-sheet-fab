package org.deku.leo2.messaging.activemq;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.transport.TransportAcceptListener;
import org.apache.activemq.transport.http.HttpTransportFactory;
import org.apache.activemq.transport.http.HttpTunnelServlet;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import java.net.URI;
import java.util.HashMap;

//@Component("JMSTunnel")
public class HttpExternalTunnelServlet extends HttpTunnelServlet
        /*implements org.springframework.web.HttpRequestHandler**/ {

    private static final long serialVersionUID = -6125839971933064732L;
    private HttpExternalTransportServer transportConnector;

    @Override
    public void init() throws ServletException {
        String url = "http://localhost:8080/jms";

        try {
            // Add the servlet connector
            transportConnector = new
                    HttpExternalTransportServer(
                    new URI(url), getServletContext());

            // Broker should not be started if we want to add the transport connector
            BrokerImpl.getInstance().addConnector(transportConnector);
            BrokerImpl.getInstance().start();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        // now lets register the listener
        // Note : normally the transport should have set the correct
        // attributes on the context during first init.
        TransportAcceptListener listener = transportConnector.getAcceptListener();
        getServletContext().setAttribute("acceptListener", listener);

        //also add some other stuff
        getServletContext().setAttribute("transportFactory", new HttpTransportFactory());
        getServletContext().setAttribute("transportOptions", new HashMap());

        super.init();
    }

//    @Override
//    /**
//     * Spring request handler support
//     */
//    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        service(request, response);
//    }
}