package sx.android.databinding

import android.databinding.BaseObservable
import android.databinding.Observable
import android.databinding.ObservableField
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject

/** Bindable field update container */
class FieldUpdate<T>(val value: T)

/**
 * Observable rx based binding field base class
 * @param bindingId The android databinding id of this field (BR)
 * @param valueSupplier The value supplier for this observable field
 */
open class RxField<T>(
        val fields: HashMap<Int, RxField<*>>,
        val bindingObservable: Observable,
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
    }

    internal fun emitValue() {
        this.subject.onNext(FieldUpdate(valueSupplier.invoke()))
    }
}

/**
 * Base observable for android data binding with support for rx observable fields.
 * Can serve as a base class for observable classes
 * Created by masc on 14.07.17.
 */
abstract class BaseRxObservable : BaseObservable() {
    /** Reactive properties for this instance */
    private val fields by lazy { HashMap<Int, RxField<*>>() }

    /**
     * Observable rx based binding field
     * @param bindingId The android databinding id of this field (BR)
     * @param valueSupplier The value supplier for this observable field
     */
    inner class ObservableRxField<T>(
            bindingId: Int,
            valueSupplier: () -> T
    ) : RxField<T>(this.fields, this, bindingId, valueSupplier)

    init {
        this.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                fields.get(propertyId)?.emitValue()
            }
        })
    }
}

/**
 * Delegating rx observable for android data binding with support for rx observable fields
 * Created by masc on 14.07.17.
 */
abstract class DelegatingRxObservable(val bindingObservable: Observable) {
    /** Reactive properties for this instance */
    private val fields by lazy { HashMap<Int, RxField<*>>() }

    /**
     * Observable rx based binding field
     * @param bindingId The android databinding id of this field (BR)
     * @param valueSupplier The value supplier for this observable field
     */
    inner class ObservableRxField<T>(
            bindingId: Int,
            valueSupplier: () -> T
    ) : RxField<T>(this.fields, bindingObservable, bindingId, valueSupplier)

    init {
        this.bindingObservable.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
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
        val field = this
        val initialValue = field.get()
        if (initialValue != null) {
            e.onNext(initialValue)
        }
        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: android.databinding.Observable, i: Int) {
                e.onNext(field.get())
            }
        }
        field.addOnPropertyChangedCallback(callback)
        e.setCancellable { field.removeOnPropertyChangedCallback(callback) }
    }
}
