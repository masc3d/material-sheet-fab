package org.deku.leo2.node;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import sx.Disposable;
import sx.LazyInstance;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by masc on 30.07.14.
 */
public class Main implements Disposable, ApplicationContextAware {
    private static LazyInstance<AtomicReference<Main>> mInstance
            = new LazyInstance<>(() -> new AtomicReference(new Main()));

    private LazyInstance<File> mLocalHomeDirectory;

    /**
     * Singleton instance
     * @return
     */
    public static Main instance() {
        return mInstance.get().get();
    }

    private Main() {
        // Spring application context
        // mContext = new AnnotationConfigApplicationContext(Main.class.getPackage().getName());

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
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
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

        System.out.println("Enter to stop webservice");
        System.in.read();

        server.stop();
    }

    @Override
    public void dispose() {
    }
}
