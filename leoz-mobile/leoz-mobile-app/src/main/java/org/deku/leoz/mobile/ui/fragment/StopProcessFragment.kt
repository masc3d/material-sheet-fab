package org.deku.leoz.mobile.ui.fragment


import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_delivery_stop_process.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Order
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ParcelListAdapter
import org.deku.leoz.mobile.ui.activity.DeliveryActivity
import org.slf4j.LoggerFactory
import sx.android.aidc.AidcReader


/**
 * A simple [Fragment] subclass.
 */
class StopProcessFragment : Fragment() {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()

    private lateinit var stop: Stop
    private lateinit var orderList: List<Order>
    private lateinit var parcelList: MutableList<Order.Parcel>
    private var lastRef: String? = null
    private var resultCount: Int = 0

    companion object {
        /**
         * Create instance with parameters. This pattern requires `retainInstance` to be set in `onCreate`!
         */
        fun create(stop: Stop): StopProcessFragment {
            val f = StopProcessFragment()
            f.stop = stop
            f.orderList = f.stop.orders.filter { it.state == Order.State.LOADED }
            f.orderList.forEach {
                f.parcelList.addAll(it.parcel)
            }
            return f
        }

        /**
         * @param orders List of orders which are supposed to be processed summarized. Note: The list should only contain orders which meet the requirements to be compressed into a single Stop
         */
        fun create(orders: List<Order>): StopProcessFragment {
            val f = StopProcessFragment()
            f.orderList = orders
            f.orderList.forEach {
                f.parcelList.addAll(it.parcel)
            }
            f.stop = f.orderList.first().toStop()
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delivery_stop_process, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RxTextView.textChanges(this.uxLabelNo).subscribe {
            this.uxLabelNo.error = null
        }

        this.uxParcelList.adapter = ParcelListAdapter(context, parcelList)

        (this.activity as DeliveryActivity).showDeliverFabButtons()
    }

    override fun onResume() {
        super.onResume()

        aidcReader.readEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    log.info("Barcode scanned ${it.data}")
                    processLabelRef(ref = it.data)
                }
    }

    private fun processLabelRef(ref: String) {
        val order: Order? = orderList.firstOrNull {
            it.parcel.firstOrNull {
                it.labelReference == ref
            } != null
        }

        hideResultImages()

        this.uxLabelNo.error = null

        if (order != null) {
            showResult(R.drawable.green)
        } else {
            //Parcel is not part of this stop
            if (lastRef.isNullOrBlank() || lastRef != ref) {
                //No (similar) reference scanned previously
                this.uxLabelNo.error = "ID does not belong to active stop."
                showResult(R.drawable.red)
            } else {
                this.uxLabelNo.error = "ID does not belong to active stop."
                showResult(R.drawable.red)
            }
        }

        lastRef = ref
    }

    private fun showResult(backgroundResource: Int) {
        hideResultImages()

        val view =  if (resultCount%2 == 0) this.uxResultLeft else this.uxResultRight

        //view.setBackgroundResource(backgroundResource)
        view.setImageDrawable(ContextCompat.getDrawable(this.context, backgroundResource))
        view.visibility = View.VISIBLE

        resultCount++
    }

    private fun hideResultImages() {
        this.uxResultLeft.visibility = View.INVISIBLE
        this.uxResultRight.visibility = View.INVISIBLE
    }

}
