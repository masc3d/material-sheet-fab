package org.deku.leoz.ui.event

import io.reactivex.subjects.PublishSubject

/**
 * Created by masc on 20/08/16.
 */
class Event<T>(
        val sender: Any,
        val value: T) {

    companion object {
        fun <T> emit(sender: Any, subject: PublishSubject<Event<T>>, value: T) {
            subject.onNext(Event(sender, value))
        }
    }
}