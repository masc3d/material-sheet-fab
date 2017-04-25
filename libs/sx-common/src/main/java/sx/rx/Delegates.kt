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
        private val subject: BehaviorSubject<T> = BehaviorSubject.create(),
        val observable: Observable<T> = subject.hide())
    :
        Observable<T>(),
        ReadWriteProperty<Any?, T> {

    private var value = default

    override fun subscribeActual(observer: Observer<in T>?) {
        this.observable.subscribe(observer)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val oldValue = this.value

        this.value = value

        if (oldValue != value)
            subject.onNext(value)
    }

    init {
        // Delegate initial/default value
        subject.onNext(default)
    }
}

/**
 * Observes property changes and delegates to rx {@link BehaviourSubject}
 */
inline fun <reified T : Any> observableRx(default: T) = ObservableRxProperty<T>(
        default = default)
