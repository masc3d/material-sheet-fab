package sx.android.rx

import android.databinding.Observable.OnPropertyChangedCallback
import org.reactivestreams.Subscriber
import android.databinding.ObservableField
import io.reactivex.Observable

/**
 * Creates rx observable for an android databinding observable field
 * Created by masc on 21.06.17.
 */
fun <T> ObservableField<T>.toObservable(): Observable<T> {
    return Observable.create {
        // Wire android databinding property change callback
        val propertyChangedCallback = object : OnPropertyChangedCallback() {
            override fun onPropertyChanged(dataBindingObservable: android.databinding.Observable, propertyId: Int) {
                if (dataBindingObservable === this) {
                    it.onNext(this@toObservable.get())
                }
            }
        }

        this.addOnPropertyChangedCallback(propertyChangedCallback)

        it.setCancellable {
            this.removeOnPropertyChangedCallback(propertyChangedCallback)
        }

        // Initial/behavioral emission
        it.onNext(this.get())
    }
}