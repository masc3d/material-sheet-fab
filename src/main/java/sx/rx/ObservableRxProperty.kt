package sx.rx

import io.reactivex.ObservableSource
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import sx.ConfigurationMap
import sx.ConfigurationMapPath
import sx.annotationOfType
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * RX property delegate implementation
 * Created by masc on 04/03/2017.
 */
class ObservableRxProperty<T>(
        default: T,
        private val subject: BehaviorSubject<ObservableRxProperty.Update<T>> = BehaviorSubject.create(),
        val observable: Observable<Update<T>> = subject.hide())
    :
        Observable<ObservableRxProperty.Update<T>>(),
        ReadWriteProperty<Any?, T> {

    /** Property update container */
    class Update<T>(val old: T?, val value: T)

    private var value = default

    override fun subscribeActual(observer: Observer<in Update<T>>) {
        this.observable.subscribe(observer)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.set(value)
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

/**
 * Observes property changes and delegates to rx {@link BehaviourSubject}
 */
inline fun <reified T : Any> observableRx(default: T) = ObservableRxProperty<T>(
        default = default)
