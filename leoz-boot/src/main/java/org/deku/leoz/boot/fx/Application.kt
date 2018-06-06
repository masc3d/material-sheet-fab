package org.deku.leoz.boot.fx

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.deku.leoz.boot.Boot
import org.deku.leoz.boot.Settings
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

/**
 * Created by masc on 28.10.17.
 */
class Application : javafx.application.Application() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Primary stage */
    private var primaryStage: Stage by Delegates.notNull()
        private set

    private val settings: Settings by Kodein.global.lazy.instance()

    override fun start(primaryStage: Stage) {
        try {
            this.primaryStage = primaryStage

            // Setup JavaFX stage
            val loader = FXMLLoader(this.javaClass.getResource("/fx/Main.fxml"))
            val root = loader.load<Parent>()

            val controller = loader.getController<MainController>()

            // Controller events
            controller.exitEvent.subscribe {
                this.exit(it)
            }

            primaryStage.title = "Leoz"
            primaryStage.scene = Scene(root, 800.0, 475.0)
            ResizeHelper.addResizeListener(primaryStage)

            val screenBounds = Screen.getPrimary().bounds
            val rootBounds = root.boundsInLocal

            val img = this.javaClass.getResourceAsStream("/images/DEKU.icon.256px.png")
            primaryStage.icons.add(Image(img))
            primaryStage.initStyle(StageStyle.UNDECORATED)
            primaryStage.y = (screenBounds.height - rootBounds.height) / 2
            primaryStage.x = (screenBounds.width - rootBounds.width) / 2
            primaryStage.show()

            // Execute boot task via main controller
            Platform.runLater {
                controller.run(
                        task = Boot().boot(settings)
                )
            }
        } catch (e: Exception) {
            log.error(e.message, e)
            this.exit(-1)
        }
    }

    /**
     * Exit application
     */
    fun exit(exitCode: Int) {
        this.primaryStage.close()

        // Platform exit code is rather slow, thus delaying invocation to prevent perceivable delay closing the stage/window
        Platform.runLater {
            Platform.exit()
            System.exit(exitCode)
        }
    }

    override fun stop() {
        super.stop()
    }
}