package org.deku.leo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.deku.leo2.fx.MainController;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        // Load main UI
        FXMLLoader fxmlMain = Global.instance().loadFxPane("/fx/Main.fxml");
        Parent root = fxmlMain.getRoot();

        // Load content pane
        FXMLLoader fxmlContent = Global.instance().loadFxPane("/fx/content/Home.fxml");
        Pane content = fxmlContent.getRoot();

        // Set content pane
        MainController mainController = fxmlMain.getController();
        mainController.setContentPane(content);

        // Main scene
        Scene scene = new Scene(root, 1600, 800);

        // Global stylesheet
        // scene.getStylesheets().add("/css/leo2.css");

        primaryStage.setTitle("leo2");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

