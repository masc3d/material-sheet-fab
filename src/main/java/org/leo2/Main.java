package org.leo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.leo2.rest.v1.*;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fx/main.fxml"));
        primaryStage.setTitle("leo2");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void testClient() {
        Client client = ClientBuilder.newClient();
        client.register(JacksonJsonProvider.class);
        WebTarget target = client.target("http://10.0.10.10:8080/leo2/v1/test");
        ITestService ts = WebResourceFactory.newResource(ITestService.class, target);

        for (int i = 0; i < 20; i++) {
            TestEntry[] entries = ts.get();

            for (TestEntry e : entries) {
                System.out.println(String.format("%d %s", i, e.toString()));
            }
        }
    }
}

