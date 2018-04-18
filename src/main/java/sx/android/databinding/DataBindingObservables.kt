package sx.android.databinding

import android.databinding.BaseObservable
import android.databinding.Observable
import android.databinding.ObservableField
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject

/** Bindable field update container */
class FieldUpdate<T>(val value: T)

/**
 * Base observable for android data binding with support for rx observable fields.
 * Can serve as a base class for observable classes
 * Created by masc on 14.07.17.
 */
abstract class BaseRxObservable : BaseObservable() {
    /** Reactive properties for this instance */
    private val fields by lazy { HashMap<Int, ObservableRxField<*>>() }

    /**
     * Observable rx based binding field
     * @param bindingId The android databinding id of this field (BR)
     * @param valueSupplier The value supplier for this observable field
     */
    inner class ObservableRxField<T>(
            bindingId: Int,
            val valueSupplier: () -> T
    ) : io.reactivex.Observable<FieldUpdate<T>>() {
        private val subject = BehaviorSubject.create<FieldUpdate<T>>()
        private val observable = subject.hide()

        override fun subscribeActual(observer: Observer<in FieldUpdate<T>>) {
            this.observable.subscribe(observer)
        }

        init {
            // Register this observable field
            fields.put(
                    bindingId,
                    this)

            this.emitValue()
        }

        /**
         * Reset observable field.
         * This may be required after refresh, as requery does not updated `@Bindable`s in this case
         */
        fun reset() {
            this.emitValue()
        }

        internal fun emitValue() {
            this.subject.onNext(FieldUpdate(valueSupplier.invoke()))
        }
    }

    init {
        this.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                fields.get(propertyId)?.emitValue()
            }
        })
    }
}

/**
 * Converts an ObservableField to an Observable. Note that setting null value inside
 * ObservableField (except for initial value) throws a NullPointerException.
 * @return Observable that contains the latest value in the ObservableField
 */
fun <T> ObservableField<T>.toObservable(): io.reactivex.Observable<T> {

    return io.reactivex.Observable.create { e ->
        val initialValue = this.get()
        if (initialValue != null) {
            e.onNext(initialValue)
        }

        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: android.databinding.Observable, i: Int) {
                e.onNext(this@toObservable.get()!!)
            }
        }

        this.addOnPropertyChangedCallback(callback)
        e.setCancellable { this.removeOnPropertyChangedCallback(callback) }
    }
}
