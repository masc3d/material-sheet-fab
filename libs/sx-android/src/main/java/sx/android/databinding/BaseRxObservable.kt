package sx.android.databinding

import android.databinding.BaseObservable
import android.databinding.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject

/**
 * Base observable for android data binding with support for rx observable fields
 * Created by masc on 14.07.17.
 */
abstract class BaseRxObservable : BaseObservable() {
    /** Reactive properties for this instance */
    private val rxProperties by lazy { HashMap<Int, ObservableRxField<*>>() }

    /** Bindable field update container */
    class FieldUpdate<T>(val value: T)

    /**
     * Observable rx based binding property
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
            rxProperties.put(
                    bindingId,
                    this)
        }

        internal fun emitValue() {
            this.subject.onNext(FieldUpdate(valueSupplier.invoke()))
        }
    }

    init {
        this.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                rxProperties
                        .get(propertyId)
                        ?.emitValue()
            }
        })
    }
}