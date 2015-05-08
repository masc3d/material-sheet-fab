package org.deku.leo2.central;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sx.Disposable;

/**
 * Created by masc on 30.07.14.
 */
public class Main implements Disposable, ApplicationContextAware {
    private volatile static Main mInstance = null;

    public static Main instance() {
        if (mInstance == null) {
            synchronized (Main.class) {
                mInstance = new Main();
            }
        }
        return mInstance;
    }

    /**
     * Spring application context
     */
    ConfigurableApplicationContext mContext;

    public ConfigurableApplicationContext getContext() {
        return mContext;
    }

    private Main() {
        // Spring application context
        mContext = new AnnotationConfigApplicationContext(Main.class.getPackage().getName());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        mContext = (ConfigurableApplicationContext)applicationContext;
    }

    public static class JettyMain {
        /**
         * Standalone jetty
         * @param args
         * @throws Exception
         */
        public static void main(String[] args) throws Exception {
            Server server = new Server(8080);

            WebAppContext context = new WebAppContext();
            context.setContextPath("/leo2");
            context.setResourceBase("src/main/webapp");
            context.setWelcomeFiles(new String[] { "index.html"} );
            server.setHandler(context);
            server.start();

            System.out.println("Enter to stop webservice");
            System.in.read();

            server.stop();
        }
    }

    @Override
    public void dispose() {
    }
}
