package org.deku.leo2.node;

import org.apache.log4j.BasicConfigurator;
import org.deku.leo2.messaging.activemq.BrokerImpl;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import sx.Disposable;
import sx.LazyInstance;

import java.io.File;
import java.util.function.Supplier;
import java.util.logging.Logger;

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

    private static Logger mLog = Logger.getLogger(App.class.getName());
    private boolean mIsInitialized;

    private LazyInstance<File> mLocalHomeDirectory;

    protected App() {
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

    public void bootstrap() {
        mLog.info("Leo2 node bootstrap");
    }

    public void initialize() throws Exception {
        mLog.info("Leo2 node initialize");

        // Disable JOOQ logo
        System.setProperty("org.jooq.no-logo", "true");

        BrokerImpl.getInstance().setDataDirectory(App.instance().getLocalHomeDirectory());
        //BrokerImpl.getInstance().start();

        mIsInitialized = true;
    }

    @Override
    public void dispose() {
        try {
            BrokerImpl.getInstance().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
