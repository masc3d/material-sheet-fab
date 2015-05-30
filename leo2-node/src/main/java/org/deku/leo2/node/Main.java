package org.deku.leo2.node;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.base.Stopwatch;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import sx.Disposable;
import sx.LazyInstance;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Bare, custom bootstrapper
 * Created by masc on 30.07.14.
 */
public class Main {
    /**
     * Main entry point
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        run(args);
    }

    public static void run(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        Global.instance().initializeLogging();

        Server server = new Server(8080);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/leo2");

        // This setup relies on the copyWebapp gradle task which copies the webapp directory into the classpath root
        String r = Main.class.getResource("/webapp").toString();
        System.out.println(r);
        String d = Main.class.getResource("/webapp/WEB-INF/web.xml").toString();
        System.out.println(d);

        context.setDescriptor(d);
        context.setResourceBase(r);

        context.setWelcomeFiles(new String[]{"index.html"});
        server.setHandler(context);
        server.start();

        //System.out.println(String.format("Started in %s. Enter to stop webservice", sw.toString()));
        //System.in.read();
        //server.stop();
    }
}
