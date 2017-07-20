package org.deku.leoz.mobile.ui

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment
import org.parceler.Parcels
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by n3 on 01/03/2017.
 */
open class Fragment : RxAppCompatDialogFragment() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val BUNDLE_PARAMETERS = "parameters"
    }

    /**
     * Kotlin property for wrapping/unwrapping parameter parcels
     */
    class ParametersProperty<T> : ReadWriteProperty<Fragment, T> {
        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            return Parcels.unwrap<T>(thisRef.arguments.getParcelable<Parcelable>(BUNDLE_PARAMETERS))
        }

        override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
            thisRef.arguments = Bundle()
            thisRef.arguments.putParcelable(BUNDLE_PARAMETERS, Parcels.wrap(value))
        }
    }

    /**
     * Observes property changes and delegates to rx {@link BehaviourSubject}
     */
    inline fun <reified T> fragmentParameters() = ParametersProperty<T>()

    /**
     * Activity
     */
    val activity: Activity
        get() = super.getActivity() as Activity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        log.trace("ONATTACH")
    }

    override fun onDetach() {
        super.onDetach()
        log.trace("ONDETACH")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.trace("ONCREATE")
    }

    override fun onDestroy() {
        super.onDestroy()
        log.trace("ONDESTROY")
    }

    override fun onPause() {
        super.onPause()
        log.trace("ONPAUSE")
    }

    override fun onResume() {
        super.onResume()
        log.trace("ONRESUME")
    }
}