package sx.rx

import io.reactivex.ObservableSource
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import sx.ConfigurationMap
import sx.ConfigurationMapPath
import sx.LazyInstance
import sx.annotationOfType
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Observable rx read-write property.
 *
 * Created by masc on 04/03/2017.
 */
class ObservableRxProperty<T>(
        default: T)
    :
        Observable<ObservableRxProperty.Update<T>>(),
        ReadWriteProperty<Any?, T> {

    /** Property update container */
    class Update<T>(val old: T?, val value: T)

    private var value = default
    private val subject: BehaviorSubject<Update<T>> = BehaviorSubject.create()
    private val observable: Observable<Update<T>> = subject.hide()

    override fun subscribeActual(observer: Observer<in Update<T>>) {
        this.observable.subscribe(observer)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.set(value)
    }

    fun get(): T {
        return value
    }

    fun set(value: T) {
        val old = this.value

        this.value = value

        if (old != value)
            subject.onNext(Update(old, value))
    }

    init {
        // Delegate initial/default value
        subject.onNext(Update(null, default))
    }
}
