package org.deku.leo2.node;

import com.google.common.base.Stopwatch;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.plugins.spring.SpringContextLoaderListener;

import javax.servlet.ServletContextListener;

/**
 * Bare, custom bootstrapper
 * Created by masc on 30.07.14.
 */
public class MainEmbedded {
    /**
     * Main entry point
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        run(args, "org.deku.leo2.node", null);
    }

    private static ContextHandler createServletContextHandler(String contextPath,
                                                              String contextConfigLocation,
                                                              ServletContextListener[] additionalListeners)
    {
        ServletContextHandler scHandler = new ServletContextHandler();
        scHandler.setContextPath(contextPath);

        scHandler.addEventListener(new ResteasyBootstrap());

        scHandler.addEventListener(new SpringContextLoaderListener());
        scHandler.setInitParameter("contextClass", "org.springframework.web.context.support.AnnotationConfigWebApplicationContext");
        scHandler.setInitParameter("contextConfigLocation", contextConfigLocation);

        scHandler.addEventListener(new org.deku.leo2.node.web.ServletContextListener());

        ServletHolder sh = scHandler.addServlet(HttpServletDispatcher.class, "/rs/api/*");
        sh.setInitParameter("resteasy.servlet.mapping.prefix", "/rs/api");
        sh.setInitParameter("javax.ws.rs.Application", "org.deku.leo2.node.rest.WebserviceApplication");
        sh.setInitOrder(1);

        if (additionalListeners != null)
            for (ServletContextListener l : additionalListeners)
                scHandler.addEventListener(l);

        return scHandler;
    }

    private static ContextHandler createStaticContentContextHandler(String contextPath, String resourceBase) {
        ResourceHandler rHandler = new ResourceHandler();
        rHandler.setWelcomeFiles(new String[]{"index.html"});
        rHandler.setDirectoriesListed(true);
        rHandler.setResourceBase(resourceBase);

        ContextHandler cHandler = new ContextHandler(contextPath);
        cHandler.setResourceBase(resourceBase);
        cHandler.setHandler(rHandler);

        return cHandler;
    }

    public static void run(String[] args, String contextConfigLocation, ServletContextListener[] listeners) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();

        Global.instance().initializeConfig();
        Global.instance().initializeLogging();
        Global.instance().initialize();

        Server server = new Server(8080);

        String contextPath = "/leo2";
        String resourceBase = Main.class.getResource("/webapp").toString();

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]
                {
                        createStaticContentContextHandler(contextPath, resourceBase),
                        createServletContextHandler(contextPath, contextConfigLocation, listeners),
                        new DefaultHandler()
                });

        server.setHandler(handlers);
        server.start();
        //server.join();

        System.out.println(String.format("Started in %s.", sw.toString()));
        //System.out.println("Enter to stop webservice");
        //System.in.read();
        //server.stop();
    }
}
