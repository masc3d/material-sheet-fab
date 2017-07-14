package sx.android.databinding

import android.databinding.Observable.OnPropertyChangedCallback
import android.databinding.ObservableField

import io.reactivex.Observable

/**
 * Converts an ObservableField to an Observable. Note that setting null value inside
 * ObservableField (except for initial value) throws a NullPointerException.
 * @return Observable that contains the latest value in the ObservableField
 */
fun <T> ObservableField<T>.toObservable(): Observable<T> {

    return Observable.create { e ->
        val field = this
        val initialValue = field.get()
        if (initialValue != null) {
            e.onNext(initialValue)
        }
        val callback = object : OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: android.databinding.Observable, i: Int) {
                e.onNext(field.get())
            }
        }
        field.addOnPropertyChangedCallback(callback)
        e.setCancellable { field.removeOnPropertyChangedCallback(callback) }
    }
}
