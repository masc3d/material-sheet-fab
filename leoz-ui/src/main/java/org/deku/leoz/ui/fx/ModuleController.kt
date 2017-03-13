package org.deku.leoz.ui.fx

import javafx.scene.image.Image
import org.deku.leoz.ui.event.BusyNotifier
import org.deku.leoz.ui.event.ErrorNotifier
import org.deku.leoz.ui.event.Event
import io.reactivex.subjects.PublishSubject

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
    override val ovBusy by lazy { PublishSubject.create<Event<Boolean>>() }

    // ErrorNotifier implementation
    override val ovError by lazy { PublishSubject.create<Exception>() }
}
