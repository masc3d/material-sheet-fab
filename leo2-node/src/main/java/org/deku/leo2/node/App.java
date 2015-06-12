package org.deku.leo2.node;

import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.activemq.ActiveMqBroker;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
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
public class App implements
        Disposable,
        // Srping won't recognize this as App is not a bean but
        // we'll inject this within web application initializer
        ApplicationContextAware {
    private static Log mLog = LogFactory.getLog(App.class);

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

    protected List<URL> mConfigLocations = new ArrayList();

    private File mLocalHomeDirectory;
    private File mLocalConfigurationFile;

    private ApplicationContext mSpringApplicationContext;

    protected App() {
        mLocalHomeDirectory = new File(System.getProperty("user.home"), ".leo2");
        mLocalConfigurationFile = new File(this.getLocalHomeDirectory(), "leo2.properties");
    }

    /**
     * Leo2 local home/data directory
     * @return
     */
    public File getLocalHomeDirectory() {
        return mLocalHomeDirectory;
    }

    /**
     * Leo2 local configuration file
     * @return
     */
    public File getLocalConfigurationFile() {
        return mLocalConfigurationFile;
    }

    public void initialize() throws Exception {
        mLog.info("Leo2 node initialize");

        // Uncaught threaded exception handler
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                mLog.error(e.getMessage(), e);
                App.this.shutdown(-1);
            }
        });

        // Disable JOOQ logo
        System.setProperty("org.jooq.no-logo", "true");

        // Set additional config file location for spring
        //region Configuration
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
            configLocations.addAll(Collections.list(Thread.currentThread().getContextClassLoader().getResources("application.properties")));
        } catch (IOException e) {
            mLog.error(e.getMessage(), e);
        }

        System.setProperty(ConfigFileApplicationListener.CONFIG_LOCATION_PROPERTY,
                String.join(",",
                        Lists.reverse(configLocations)
                        .stream()
                        .map(u -> u.toString()).toArray(size -> new String[size])));
        //endregion

        // Initialize broker
        ActiveMqBroker.instance().setDataDirectory(
                App.instance().getLocalHomeDirectory());
    }

    @Override
    public void dispose() {
        try {
            ActiveMqBroker.instance().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shutdown application
     * @param exitCode Exit code
     */
    public void shutdown(int exitCode) {
        if (mSpringApplicationContext != null) {
            SpringApplication.exit(mSpringApplicationContext);
        }
        System.exit(0);
    }
    public void shutdown() {
        this.shutdown(0);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        mSpringApplicationContext = applicationContext;
    }

    public ApplicationContext getSpringApplicationContext() {
        return mSpringApplicationContext;
    }
}
