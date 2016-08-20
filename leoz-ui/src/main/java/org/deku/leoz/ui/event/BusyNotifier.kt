package org.deku.leoz.ui.event

import org.deku.leoz.ui.event.Event
import rx.Observable
import rx.subjects.PublishSubject
import java.lang.ref.WeakReference

/**
 * Busy notifier interface.
 * Created by masc on 20/08/16.
 */
interface BusyNotifier {
    val ovBusy: PublishSubject<Event<Boolean>>
}

/**
 * Wraps a block with busy events
 */
inline fun <T> BusyNotifier.busy(r: () -> T): T {
    try {
        Event.emit(this, this.ovBusy, true)
        return r()
    } finally {
        Event.emit(this, this.ovBusy, false)
    }
}