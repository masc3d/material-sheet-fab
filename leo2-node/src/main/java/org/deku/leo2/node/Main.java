package org.deku.leo2.node;

import com.google.common.base.Stopwatch;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

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
