package sx.android.databinding

import android.databinding.ObservableField
import android.util.Log

import java.util.HashMap

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.disposables.Disposable
import org.slf4j.LoggerFactory
import sx.android.rx.observeOnMainThread
import sx.rx.ObservableRxProperty

/**
 * Android data binding field wrapping a regular rx Observable (readonly) or ObservableRxProperty (readwrite)
 * @param source Source observable
 * @param default Field default value
 */
class ObservableRxField<T> constructor(
        source: Observable<T>,
        default: T? = null)
    : ObservableField<T>(default) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val source: Observable<T>
    private val subscriptions = HashMap<android.databinding.Observable.OnPropertyChangedCallback, Disposable>()

    init {
        this.source = source
                .observeOnMainThread()
                .share()

        this.source.subscribe {
            super@ObservableRxField.set(it)
        }
    }

    override fun addOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback) {
        super.addOnPropertyChangedCallback(callback)
        subscriptions.put(callback, source.subscribe())
    }

    override fun removeOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback) {
        super.removeOnPropertyChangedCallback(callback)
        val subscription = subscriptions.remove(callback)
        if (subscription != null && !subscription.isDisposed) {
            subscription.dispose()
        }
    }
}


/**
 * Extension method for creating ObservableRxField from rx observable
 * @param default Default value
 * @return DataBinding field created from the specified Observable
 */
fun <T> Observable<T>.toField(default: T? = null): ObservableRxField<T>
        = ObservableRxField(source = this, default = default)