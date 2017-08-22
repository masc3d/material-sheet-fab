package sx.rx

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject

/**
 * Decorates a mutable list and emits changed events
 *
 * As with most regular list classes, this implementation is NOT thread-safe
 *
 * Created by masc on 26.07.17.
 */
class ObservableRxList<T>(val list: MutableList<T>)
    : MutableList<T> by list {

    enum class UpdateType {
        ADDED,
        REMOVED
    }

    inner class Update<T>(
            val type: UpdateType,
            val items: List<T>
    )

    private val updatedEventSubject = BehaviorSubject.createDefault<Update<T>>(
            Update(type = UpdateType.ADDED, items = this.list))

    val updatedEvent = updatedEventSubject.hide()

    /**
     * Transforms observable list to a plain observable emitting updates
     */
    fun toObservable(): Observable<Update<T>> {
        return this.updatedEvent
    }

    override fun add(element: T): Boolean {
        val changed = this.list.add(element)

        if (changed) {
            this.updatedEventSubject.onNext(Update(
                    type = UpdateType.ADDED,
                    items = listOf(element)))
        }

        return changed
    }

    override fun add(index: Int, element: T) {
        this.list.add(index, element)

        this.updatedEventSubject.onNext(Update(
                type = UpdateType.ADDED,
                items = listOf(element)))
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        val changed = this.list.addAll(index, elements)

        if (changed) {
            this.updatedEventSubject.onNext(Update(
                    type = UpdateType.ADDED,
                    items = elements.toList()))
        }

        return changed
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val changed = this.list.addAll(elements)

        if (changed) {
            this.updatedEventSubject.onNext(Update(
                    type = UpdateType.ADDED,
                    items = elements.toList()))
        }

        return changed
    }

    override fun clear() {
        this.list.clear()

        this.updatedEventSubject.onNext(Update(
                type = UpdateType.REMOVED,
                items = this.list))
    }

    override fun remove(element: T): Boolean {
        val changed = this.list.remove(element)

        if (changed) {
            this.updatedEventSubject.onNext(Update(
                    type = UpdateType.REMOVED,
                    items = listOf(element)))
        }

        return changed
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val changed = this.list.removeAll(elements)

        if (changed) {
            this.updatedEventSubject.onNext(Update(
                    type = UpdateType.REMOVED,
                    items = elements.toList()))
        }

        return changed
    }

    override fun removeAt(index: Int): T {
        val element = this.list.removeAt(index)

        this.updatedEventSubject.onNext(Update(
                type = UpdateType.REMOVED,
                items = listOf(element)))

        return element
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        // Create copy of current items
        val previousItems =  this.list.toList()
        val changed = this.list.retainAll(elements)

        if (changed) {
            this.updatedEventSubject.onNext(Update(
                    type = UpdateType.REMOVED,
                    items = this.list.subtract(previousItems).toList()))
        }

        return changed
    }

    override fun set(index: Int, element: T): T {
        val previousElement = this.list.set(index, element)

        this.updatedEventSubject.onNext(Update(
                type = UpdateType.REMOVED,
                items = listOf(previousElement)))

        this.updatedEventSubject.onNext(Update(
                type = UpdateType.ADDED,
                items = listOf(element)))

        return previousElement
    }
}