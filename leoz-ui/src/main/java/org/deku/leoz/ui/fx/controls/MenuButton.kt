package org.deku.leoz.ui.fx.controls

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.image.ImageView

import java.io.IOException

/**
 * Created by masc on 13.09.14.
 */
class MenuButton : Button() {
    @FXML
    private lateinit var fxImageView: ImageView

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("../../../../../../resources/fx/controls/MenuButton.fxml"))
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        try {
            fxmlLoader.load<Any>()
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }

    }

//    fun onButton(e: ActionEvent) {
//    }
}
