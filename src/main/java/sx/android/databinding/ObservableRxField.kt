package sx.android.databinding

import android.databinding.ObservableField
import android.util.Log

import java.util.HashMap

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import sx.rx.ObservableRxProperty

/**
 * Android data binding field wrapping a regular rx Observable (readonly) or ObservableRxProperty (readwrite)
 */
class ObservableRxField<T> private constructor(
        source: Observable<T>,
        property: ObservableRxProperty<T>? = null
)
    : ObservableField<T>() {

    private val source: Observable<T>
    private val property: ObservableRxProperty<T>?
    private val subscriptions = HashMap<android.databinding.Observable.OnPropertyChangedCallback, Disposable>()

    /** Constructor for regular observable (read-only field) */
    constructor(source: Observable<T>): this(
            source = source,
            property = null)

    /** Constructor for rx property (read-write field) */
    constructor(property: ObservableRxProperty<T>): this(
            source = property.map { it.value },
            property = property)

    init {
        this.source = source
                .doOnNext { t -> super@ObservableRxField.set(t) }
                .onErrorResumeNext(Observable.empty<T>())
                .share()

        this.property = property
    }

    override fun set(value: T) {
        val property = this.property
        if (property == null)
            throw IllegalStateException("Field is not backed by rx property, thus read-only")

        property.set(value)

    }

    @Synchronized override fun addOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback) {
        super.addOnPropertyChangedCallback(callback)
        subscriptions.put(callback, source.subscribe())
    }

    @Synchronized override fun removeOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback) {
        super.removeOnPropertyChangedCallback(callback)
        val subscription = subscriptions.remove(callback)
        if (subscription != null && !subscription.isDisposed) {
            subscription.dispose()
        }
    }
}


/**
 * Extension method for creating ObservableRxField from rx observable
 * @return DataBinding field created from the specified Observable
 */
fun <T> Observable<T>.toField(): ObservableRxField<T> {
    return ObservableRxField(this)
}

/**
 * Extension method for creating ObservableRxField from rx observable property
 * @return DataBinding field created from the specified ObservableRxProperty
 */
fun <T> ObservableRxProperty<T>.toField(): ObservableRxField<T> {
    return ObservableRxField(this)
}