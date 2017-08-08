package org.deku.leoz.mobile.ui

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment
import org.parceler.Parcels
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Created by n3 on 01/03/2017.
 */
open class Fragment<P> : RxAppCompatDialogFragment() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val BUNDLE_PARAMETERS = "parameters"

        /**
         * Factory method
         */
        inline fun <F : Fragment<P>, reified P> create(c: KClass<F>, parameters: P): F {
            val f = c.java.newInstance()
            f.parameters = parameters
            return f
        }
    }

    /**
     * Kotlin property for wrapping/unwrapping parameter parcels
     */
    private class ParametersProperty<P> : ReadWriteProperty<Fragment<P>, P> {
        override fun getValue(thisRef: Fragment<P>, property: KProperty<*>): P {
            return Parcels.unwrap<P>(thisRef.arguments.getParcelable<Parcelable>(BUNDLE_PARAMETERS))
        }

        override fun setValue(thisRef: Fragment<P>, property: KProperty<*>, value: P) {
            thisRef.arguments = Bundle()
            thisRef.arguments.putParcelable(BUNDLE_PARAMETERS, Parcels.wrap(value))
        }
    }

    /**
     * Fragment parameters
     */
    var parameters by ParametersProperty()

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