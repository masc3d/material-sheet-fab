package org.deku.leoz.mobile.ui.fragment


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.android.synthetic.main.fragment_main.*
import org.deku.leoz.mobile.BuildConfig

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Job
import org.deku.leoz.mobile.ui.DeliveryMenuListAdapter
import org.slf4j.LoggerFactory


/**
 * A simple [Fragment] subclass.
 */
class DeliveryFragment : Fragment() {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val job: Job by Kodein.global.lazy.instance()

    private var mListener: OnDeliveryMenuChoosed? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        // Reusing fragment_main (trying)
        return inflater!!.inflate(R.layout.fragment_delivery, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxVersion?.text = "v${BuildConfig.VERSION_NAME}"

    }

    fun onEntryPressed(entry: DeliveryMenuListAdapter.DeliveryMenuEntry) {
        if (mListener != null) {
            mListener!!.onDeliveryMenuChoosed(entry.entryType)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is DeliveryFragment.OnDeliveryMenuChoosed) {
            mListener = context as DeliveryFragment.OnDeliveryMenuChoosed?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnDeliveryMenuChoosed")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnDeliveryMenuChoosed {
        // TODO: Update argument type and name
        fun onDeliveryMenuChoosed(entryType: DeliveryMenuListAdapter.DeliveryMenuEntry.Entry)
    }

}
