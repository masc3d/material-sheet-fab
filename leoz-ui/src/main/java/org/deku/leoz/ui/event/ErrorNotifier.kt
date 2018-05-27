package org.deku.leoz.ui.event

import io.reactivex.subjects.PublishSubject

/**
 * Error notifier interface.
 * Created by masc on 20/08/16.
 */
interface ErrorNotifier {
    val ovError: PublishSubject<Exception>
}
