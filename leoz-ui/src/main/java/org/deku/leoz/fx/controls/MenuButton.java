package org.deku.leoz.fx.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.io.IOException;

/**
 * Created by masc on 13.09.14.
 */
public class MenuButton extends Button {

    @FXML
    ImageView mImageView;

    public MenuButton() {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../../../../../resources/fx/controls/MenuButton.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void onButton(ActionEvent e) {

    }
}
