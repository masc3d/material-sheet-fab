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
    javafx.application.Application.launch(Application::class.java)
}

/**
 * Created by n3 on 29-Jul-15.
 */
class Application : javafx.application.Application() {
    @Throws(Exception::class)

    override fun start(primaryStage: Stage) {
        var splash = SplashScreen.getSplashScreen()

        val root = FXMLLoader.load<Parent>(this.javaClass.getResource("/fx/Main.fxml"))
        primaryStage.title = "LeoZ Boot"
        primaryStage.scene = Scene(root, 600.0, 275.0)

        var screenBounds = Screen.getPrimary().bounds
        var rootBounds = root.boundsInLocal

        var img = this.javaClass.getResourceAsStream("/images/DEKU.icon.256px.png")
        primaryStage.icons.add(Image(img))
        primaryStage.y = (screenBounds.height - rootBounds.height) / 2
        primaryStage.x = (screenBounds.width - rootBounds.width) / 2
        primaryStage.show()

        if (splash != null) {
            splash.close()
        }
    }

    companion object {
    }
}