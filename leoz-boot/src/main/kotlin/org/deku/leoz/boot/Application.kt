package org.deku.leoz.boot

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Screen
import javafx.stage.Stage
import java.awt.SplashScreen
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    javafx.application.Application.launch(javaClass<Application>())
}

/**
 * Created by n3 on 29-Jul-15.
 */
class Application : javafx.application.Application() {
    throws(Exception::class)

    override fun start(primaryStage: Stage) {
        var splash = SplashScreen.getSplashScreen()

        val root = FXMLLoader.load<Parent>(this.javaClass.getResource("/fx/Main.fxml"))
        primaryStage.setTitle("LeoZ Boot")
        primaryStage.setScene(Scene(root, 600.0, 275.0))

        var screenBounds = Screen.getPrimary().getBounds()
        var rootBounds = root.getBoundsInLocal()

        var img = this.javaClass.getResourceAsStream("/images/DEKU.icon.256px.png")
        primaryStage.getIcons().add(Image(img))
        primaryStage.setY((screenBounds.getHeight() - rootBounds.getHeight()) / 2)
        primaryStage.setX((screenBounds.getWidth() - rootBounds.getWidth()) / 2)
        primaryStage.show()

        if (splash != null) {
            splash.close()
        }
    }

    companion object {
    }
}