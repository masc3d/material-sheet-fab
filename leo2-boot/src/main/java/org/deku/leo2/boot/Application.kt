package org.deku.leo2.boot

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Created by n3 on 29-Jul-15.
 */
class Application : javafx.application.Application() {
    throws(Exception::class)

    override fun start(primaryStage: Stage) {
        val root = FXMLLoader.load<Parent>(this.javaClass.getResource("fx/Main.fxml"))
        primaryStage.setTitle("LeoZ Boot")
        primaryStage.setScene(Scene(root, 600.0, 275.0))
        primaryStage.show()
    }

    companion object {
    }
}