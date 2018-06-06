package org.deku.leoz.boot.fx

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.github.thomasnield.rxkotlinfx.observeOnFx
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import org.deku.leoz.boot.Boot
import org.deku.leoz.boot.Settings
import org.deku.leoz.boot.config.LogConfiguration
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import sx.JarManifest
import sx.fx.TextAreaLogAppender
import sx.fx.controls.MaterialProgressIndicator
import sx.platform.JvmUtil
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
    lateinit var uxProgressIndicator: MaterialProgressIndicator
    @FXML
    lateinit var uxClose: Button

    // Injections
    private val logConfiguration: LogConfiguration by Kodein.global.lazy.instance()
    private val jarManifest: JarManifest by Kodein.global.lazy.instance()
    private val settings: Settings by Kodein.global.lazy.instance()
    private val executorService: ScheduledExecutorService by Kodein.global.lazy.instance()

    private var logAppender: TextAreaLogAppender? = null

    private val exitEventSubject = PublishSubject.create<Int>()
    /** Exit event */
    val exitEvent by lazy { exitEventSubject.hide() }

    private var exitCode: Int = 0

    /**
     * Run task
     */
    fun run(task: Observable<Boot.Event>) {
        var verb: String
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
                .observeOnFx()
                .subscribeBy(
                    onNext = {
                        uxProgressBar.progress = it.progress
                    },
                    onComplete = {
                        uxTitle.text = "${verbPast} succesfully."
                        uxProgressBar.styleClass.add("leoz-green-bar")
                        uxProgressBar.progress = 1.0
                        uxClose.visibleProperty().value = true
                    },
                    onError = {
                        this@MainController.exitCode = -1
                        log.error(it.message, it)
                        uxTitle.text = "${verb} failed."
                        uxProgressBar.styleClass.add("leoz-red-bar")
                        uxProgressBar.progress = 1.0
                        uxClose.visibleProperty().value = true
                    })
    }

    /**
     * Initialize
     */
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        uxTitle.text = ""
        uxProgressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS)
        uxProgressBar.progressProperty().addListener { _, _, n ->
            val progress = n.toDouble()
            val visible = (progress == ProgressBar.INDETERMINATE_PROGRESS || (progress >= 0.0 && progress < 1))
            uxProgressIndicator.isVisible = visible
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