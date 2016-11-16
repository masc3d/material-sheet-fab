package org.deku.leoz.boot.fx

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.google.common.base.Strings
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import org.deku.leoz.boot.Boot
import org.deku.leoz.boot.Settings
import org.deku.leoz.boot.config.LogConfiguration
import rx.Observable
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.subscribeWith
import rx.schedulers.JavaFxScheduler
import rx.schedulers.Schedulers
import sx.JarManifest
import sx.fx.TextAreaLogAppender
import sx.platform.JvmUtil
import java.awt.GraphicsEnvironment
import java.net.URL
import java.util.*
import java.util.concurrent.ScheduledExecutorService

/**
 * Created by masc on 29-Jul-15.
 */
class MainController : Initializable {
    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    // JavaFX
    @FXML
    lateinit var uxTitle: Label
    @FXML
    lateinit var uxTextArea: TextArea
    @FXML
    lateinit var uxProgressBar: ProgressBar
    @FXML
    lateinit var uxProgressIndicator: ProgressIndicator
    @FXML
    lateinit var uxClose: Button

    // Injections
    private val logConfiguration: LogConfiguration by Kodein.global.lazy.instance()
    private val jarManifest: JarManifest by Kodein.global.lazy.instance()
    private val settings: Settings by Kodein.global.lazy.instance()
    private val executorService: ScheduledExecutorService by Kodein.global.lazy.instance()

    private var logAppender: TextAreaLogAppender? = null

    private val exitEventSubject = PublishSubject<Int>()
    /** Exit event */
    val exitEvent by lazy { exitEventSubject.asObservable() }

    private var exitCode: Int = 0

    /**
     * Run task
     */
    fun run(task: Observable<Boot.Event>) {
        var verb: String = "Initializing"
        val verbPast: String

        val bundleName = this.settings.bundle

        when (this.settings.uninstall) {
            true -> {
                verb = "Uninstalling"
                verbPast = "Uninstalled"
            }
            else -> {
                verb = "Booting"
                verbPast = "Booted"
            }
        }
        verb += " ${bundleName}"

        log.info(verb)
        uxTitle.text = verb

        task
                .subscribeOn(Schedulers.from(this.executorService))
                .observeOn(JavaFxScheduler.getInstance())
                .subscribeWith {
                    onNext {
                        uxProgressBar.progress = it.progress
                    }
                    onCompleted {
                        uxTitle.text = "${verbPast} succesfully."
                        uxProgressBar.styleClass.add("leoz-green-bar")
                        uxProgressBar.progress = 1.0
                        uxClose.visibleProperty().value = true
                    }
                    onError {
                        this@MainController.exitCode = -1
                        log.error(it.message, it)
                        uxTitle.text = "${verb} failed."
                        uxProgressBar.styleClass.add("leoz-red-bar")
                        uxProgressBar.progress = 1.0
                        uxClose.visibleProperty().value = true
                    }
                }
    }

    /**
     * Initialize
     */
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        uxTitle.text = ""
        uxProgressBar.progressProperty().addListener { v, o, n ->
            uxProgressIndicator.isVisible = (n.toDouble() == ProgressBar.INDETERMINATE_PROGRESS || (n.toDouble() >= 0.0 && n.toDouble() < 1))
        }
        uxClose.onMouseClicked = object : EventHandler<MouseEvent> {
            override fun handle(event: MouseEvent?) {
                exitEventSubject.onNext(exitCode)
            }
        }
        uxClose.visibleProperty().value = false
        uxProgressBar.progress = ProgressBar.INDETERMINATE_PROGRESS

        this.logConfiguration.addAppender(TextAreaLogAppender(uxTextArea, 1000))

        log.info(JvmUtil.shortInfoText)

        try {
            log.info("leoz-boot [${this.jarManifest.implementationVersion}] ${JvmUtil.shortInfoText}")
        } catch(e: Exception) {
            // Printing jar manifest will fail when running from IDE eg. that's ok.
        }
    }
}