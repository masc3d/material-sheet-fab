package org.deku.leoz.ui.fx

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.image.Image
import org.deku.leoz.ui.Localization
import org.deku.leoz.ui.event.BusyNotifier
import org.deku.leoz.ui.event.Event
import org.deku.leoz.ui.Main
import org.deku.leoz.ui.event.ErrorNotifier
import rx.lang.kotlin.PublishSubject
import rx.subjects.PublishSubject

/**
 * Base class for leoz user interface modules

 * Created by masc on 23.09.14.
 */
abstract class ModuleController : Controller(), BusyNotifier, ErrorNotifier {
    /**
     * Module title
     * @return
     */
    abstract val title: String

    /**
     * Module title image
     * @return
     */
    abstract val titleImage: Image

    // BusyNotifier implementation
    override val ovBusy by lazy { PublishSubject<Event<Boolean>>() }

    // ErrorNotifier implementation
    override val ovError by lazy { PublishSubject<Exception>() }
}
