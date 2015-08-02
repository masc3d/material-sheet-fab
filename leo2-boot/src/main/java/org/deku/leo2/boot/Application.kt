package org.deku.leo2.boot

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

fun main(args: Array<String>) {
    javafx.application.Application.launch(javaClass<Application>())
}

/**
 * Created by n3 on 29-Jul-15.
 */
class Application : javafx.application.Application() {
    throws(Exception::class)

    override fun start(primaryStage: Stage) {
        val root = FXMLLoader.load<Parent>(this.javaClass.getResource("/fx/Main.fxml"))
        primaryStage.setTitle("LeoZ Boot")
        primaryStage.setScene(Scene(root, 600.0, 275.0))

        var img = this.javaClass.getResourceAsStream("/images/DEKU.icon.256px.png")
        primaryStage.getIcons().add(Image(img))

        primaryStage.show()
    }

    companion object {
    }
}