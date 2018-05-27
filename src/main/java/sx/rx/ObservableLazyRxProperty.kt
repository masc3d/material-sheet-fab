package sx.rx

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject
import sx.LazyInstance
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Lazy observable rx property.
 *
 * Created by masc on 04/03/2017.
 */
class ObservableLazyRxProperty<T>(
        default: () -> T)
    :
        Observable<ObservableLazyRxProperty.Update<T>>(),
        ReadOnlyProperty<Any?, T> {

    /** Property update container */
    class Update<T>(val value: T)

    private var value = LazyInstance(default, LazyInstance.ThreadSafetyMode.None)
    private val subject: BehaviorSubject<Unit> = BehaviorSubject.create()
    private val observable: Observable<Unit> = subject.hide()

    override fun subscribeActual(observer: Observer<in Update<T>>) {
        this.observable
                .map { Update(this.value.get()) }
                .subscribe(observer)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value.get()

    fun get(): T = this.value.get()

    fun reset(supplier: (() -> T)? = null) {
        this.value.reset(supplier)
        this.subject.onNext(Unit)
    }

    init {
        this.reset()
    }
}


