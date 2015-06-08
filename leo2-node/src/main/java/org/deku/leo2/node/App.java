package org.deku.leo2.node;

import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.deku.leo2.messaging.Context;
import org.deku.leo2.messaging.activemq.BrokerImpl;
import org.deku.leo2.messaging.activemq.ContextImpl;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import sx.Disposable;
import sx.LazyInstance;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by masc on 30.05.15.
 */
public class App implements Disposable, ApplicationContextAware {

    //region Singleton
    private static LazyInstance<App> mInstance = new LazyInstance(App::new);

    /**
     * Singleton instance
     * @return
     */
    public static App instance() {
        return mInstance.get();
    }

    /**
     * Static injection
     * @param supplier
     */
    public static void inject(Supplier<App> supplier) {
        mInstance.set(supplier);
    }
    //endregion

    private static Log mLog = LogFactory.getLog(App.class);
    private boolean mIsInitialized;

    private LazyInstance<File> mLocalHomeDirectory;
    private LazyInstance<File> mLocalConfigurationFile;

    protected App() {
        mLocalHomeDirectory = new LazyInstance<>( () ->  new File(System.getProperty("user.home"), ".leo2"));
        mLocalConfigurationFile = new LazyInstance<>( () -> new File(mLocalHomeDirectory.get(), "leo2.properties"));
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

    /**
     * Leo2 local home/data directory
     * @return
     */
    public File getLocalHomeDirectory() {
        return mLocalHomeDirectory.get();
    }

    /**
     * Leo2 local configuration file
     * @return
     */
    public File getLocalConfigurationFile() {
        return mLocalConfigurationFile.get();
    }


    public boolean isInitialized() {
        return mIsInitialized;
    }

    /**
     * Intialize logging.
     * Not required for spring-boot.
     */
    public void initializeLogging() {
        // Log4j logging (to make resteasy log entries visible, needs refinement)
        BasicConfigurator.configure();

        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * Not required for spring-boot.
     */
    public void initializeConfig() {
//        Yaml y = new Yaml();
//        Object c = y.load(this.getClass().getClassLoader().getResourceAsStream("application.yml"));
    }

    public void initialize() {
        mLog.info("Leo2 node initialize");

        // Disable JOOQ logo
        System.setProperty("org.jooq.no-logo", "true");

        // Set additional config file location for spring
        List<URL> configLocations = new ArrayList<>();

        // Add local home configuration
        try {
            configLocations.add(new URL("file:" + this.getLocalConfigurationFile().toString()));
        } catch (MalformedURLException e) {
            mLog.error(e.getMessage(), e);
        }

        // Add application.properties from all classpaths
        // TODO: needs refinement, should only read application.properties from specific jars
        try {
            configLocations.addAll(Collections.list(Main.class.getClassLoader().getResources("application.properties")));
        } catch (IOException e) {
            mLog.error(e.getMessage(), e);
        }

        System.setProperty(ConfigFileApplicationListener.CONFIG_LOCATION_PROPERTY,
                String.join(",",
                        Lists.reverse(configLocations)
                        .stream()
                        .map(u -> u.toString()).toArray(size -> new String[size])));

        ContextImpl.instance().getBroker().setDataDirectory(App.instance().getLocalHomeDirectory());
        //BrokerImpl.getInstance().start();

        mIsInitialized = true;

        //throw new IllegalStateException();
    }

    @Override
    public void dispose() {
        try {
            ContextImpl.instance().getBroker().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
