package org.deku.leo2.node;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.messaging.log.LogAppender;
import org.deku.leo2.node.auth.IdentityConfiguration;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import sx.Disposable;
import sx.LazyInstance;
import sx.jms.embedded.activemq.ActiveMQBroker;

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
        ApplicationContextAware,
        ApplicationListener {
    /** Logger */
    private static Log mLog = LogFactory.getLog(App.class);

    /** Client node profile, activates specific configurations for leo2 client nodes */
    public static final String PROFILE_CLIENT_NODE = "client-node";

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

    private ApplicationContext mSpringApplicationContext;

    private ArrayList<Disposable> mDisposables = new ArrayList<>();
    private volatile boolean mIsShuttingDown;
    private volatile boolean mIsInitialized;

    private String mProfile;

    public String getProfile() {
        return mProfile;
    }

    /** c'tor */
    protected App() {
    }

    private Runnable mConfigureLoggingFunc = () -> {
    };

    /**
     * Intialize application
     * @param profile Spring profile name
     */
    public void initialize(String profile) {

        if (mIsInitialized)
            throw new IllegalStateException("Application already initialized");
        mIsInitialized = true;

        mProfile = profile;

        // Initialize local storage
        LocalStorage.instance().initialize();

        // Initialize logging
        if (mProfile == PROFILE_CLIENT_NODE) {
            // Setup message log appender
            Logger lRoot = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            LogAppender lAppender = new LogAppender(ActiveMQContext.instance());

            mConfigureLoggingFunc = () -> {
                LoggerContext lContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                // Configure jms log appender
                lAppender.setContext(lContext);
                lAppender.start();
                lRoot.addAppender(lAppender);
            };
            mConfigureLoggingFunc.run();
            mDisposables.add(() -> {
                lRoot.detachAppender(lAppender);
                lAppender.dispose();
            });
        }
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
            configLocations.add(new URL("file:" + LocalStorage.instance().getApplicationConfigurationFile().toString()));
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

        // Basic broker configuration
        ActiveMQBroker.instance().setDataDirectory(
                new File(LocalStorage.instance().getHomeDirectory(), "activemq"));

        Runtime.getRuntime().addShutdownHook(new Thread("App shutdown hook") {
            @Override
            public void run() {
                App.instance().dispose();
            }
        });
    }

    public void initialize() {
        this.initialize(PROFILE_CLIENT_NODE);
    }

    @Override
    public void dispose() {
        for (Disposable d : new ArrayList<Disposable>(mDisposables)) {
            try {
                mLog.info(String.format("Disposing %s", d.getClass().getName()));
                d.dispose();
            } catch (Exception e) {
                mLog.error(e.getMessage(), e);
            }
        }
        try {
            ActiveMQBroker.instance().stop();
        } catch (Exception e) {
            mLog.error(e.getMessage(), e);
        }
    }

    /**
     * Shutdown application
     * @param exitCode Exit code
     */
    public void shutdown(int exitCode) {
        if (mIsShuttingDown) {
            mLog.warn("Already shutting down");
            return;
        }

        mIsShuttingDown = true;
        mLog.info("Shutting down");
        System.exit(exitCode);
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


    @Override
    public final void onApplicationEvent(ApplicationEvent event) {
        mLog.trace(String.format("Spring application event: %s",
                event.getClass().getSimpleName()));

        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            // Spring resets logging configuration.
            // As we don't want to supply a logging framework specific config file, simply reapplying
            // logging configuration after spring environment has been prepared.
            mConfigureLoggingFunc.run();
        } else if (event instanceof ApplicationPreparedEvent) {
            // Initialize identity
            IdentityConfiguration.instance().initialize();

            //endregion
        } else if (event instanceof EmbeddedServletContainerInitializedEvent) {
            // Post spring initialization
        }
    }
}
