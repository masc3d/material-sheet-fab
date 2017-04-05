package sx.rx

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
        private val default: T,
        private val subject: BehaviorSubject<T>,
        private val type: Class<T>)
    :
        ObservableProperty<T>(default) {

    init {
        // Delegate initial/default value
        subject.onNext(default)
    }

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        super.afterChange(property, oldValue, newValue)
        // Delegate property value change
        if (oldValue != newValue)
            subject.onNext(newValue)
    }
}

/**
 * Observes property changes and delegates to rx {@link BehaviourSubject}
 */
inline fun <reified T : Any> observableRx(default: T, subject: BehaviorSubject<T>) = ObservableRxProperty<T>(
        default = default,
        subject = subject,
        type = T::class.java)