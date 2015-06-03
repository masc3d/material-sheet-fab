package org.deku.leo2.central;

import org.apache.activemq.transport.http.HttpTunnelServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import sx.Disposable;
import sx.LazyInstance;

import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * leo2-central main class.
 *
 * Derives from node's main class.
 * Requires @Configuration to pull in spring components configured via base class.
 *
 * Created by masc on 30.07.14.
 */
public class MainEmbedded {
    /**
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        org.deku.leo2.node.MainEmbedded.run(args, "org.deku.leo2.central", new ServletContextListener[] {
                        new org.deku.leo2.central.web.ServletContextListener()
                }
        );
    }
}
