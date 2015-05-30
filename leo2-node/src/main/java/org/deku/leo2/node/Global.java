package org.deku.leo2.node;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
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
 * Created by masc on 30.05.15.
 */
@Configuration("node.Global")
@ComponentScan
public class Global implements Disposable, ApplicationContextAware {
    private static AtomicReference<LazyInstance<Global>> mInstance
            = new AtomicReference<>(new LazyInstance(() -> new Global()));

    private LazyInstance<File> mLocalHomeDirectory;

    /**
     * Singleton instance
     * @return
     */
    public static Global instance() {
        return mInstance.get().get();
    }

    public Global() {
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
    public void initializeLogging() {
        // Disable JOOQ logo
        System.setProperty("org.jooq.no-logo", "true");
        // SLF4J
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        // Log4j logging (to make resteasy log entries visible, needs refinement)
        BasicConfigurator.configure();
    }

    @Override
    public void dispose() {

    }
}
