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
 * Created by masc on 30.07.14.
 */
@Configuration("node.Main")
@ComponentScan
public class Main implements Disposable, ApplicationContextAware {
    private static AtomicReference<LazyInstance<Main>> mInstance
            = new AtomicReference<>(new LazyInstance(() -> new Main()));

    private LazyInstance<File> mLocalHomeDirectory;

    /**
     * Singleton instance
     * @return
     */
    public static Main instance() {
        return mInstance.get().get();
    }

    public Main() {
        // Spring application context
        // mContext = new AnnotationConfigApplicationContext(Main.class.getPackage().getName());
        mInstance.set(new LazyInstance(() -> this));
        mLocalHomeDirectory = new LazyInstance<>( () ->  new File(System.getProperty("user.home"), ".leo2"));
    }

    /**
     * Spring application context
     */
    ConfigurableApplicationContext mContext;

    public ConfigurableApplicationContext getContext() {
        return mContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        mContext = (ConfigurableApplicationContext)applicationContext;
    }

    public File getLocalHomeDirectory() {
        return mLocalHomeDirectory.get();
    }

    /**
     * Intialize logging
     */
    public static void initializeLogging() {
        // SLF4J
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        // Log4j logging (to make resteasy log entries visible, needs refinement)
        BasicConfigurator.configure();
    }

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
        Main.initializeLogging();

        Server server = new Server(8080);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/leo2");

        // This setup relies on the copyWebapp gradle task which copies the webapp directory into the classpath root
        String r = Main.class.getResource("/webapp").toString();
        System.out.println(r);
        String d = Main.class.getResource("/webapp/WEB-INF/web-leo2.xml").toString();
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

    @Override
    public void dispose() {
    }
}
