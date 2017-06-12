package org.deku.leoz.mobile.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_delivery_process.*
import kotlinx.android.synthetic.main.main_app_bar.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Order
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.ParcelListAdapter
import org.slf4j.LoggerFactory
import sx.android.aidc.AidcReader
import java.text.SimpleDateFormat


class DeliveryProcessFragment(val stop: Stop) : Fragment() {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()

    private var lastRef: String? = null
    private var resultCount: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_delivery_process, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")

        this.uxStreet.text = stop.address.street
        this.uxStreetNo.text = stop.address.streetNo
        this.uxZip.text = stop.address.zipCode
        this.uxCity.text = stop.address.city
        this.uxReceipient.text = stop.address.addressLine1
        this.uxAppointment.text = "${simpleDateFormat.format(stop.appointment.dateFrom)} - ${simpleDateFormat.format(stop.appointment.dateTo)}"

        RxTextView.textChanges(this.uxLabelNo).subscribe {
            this.uxLabelNo.error = null
        }

        //region TODO To be replaced by a custom adapter
        val parcelList = mutableListOf<Order.Parcel>()

        stop.order.forEach {
            parcelList.addAll(it.parcel)
        }

        val parcelRefList: List<String> = parcelList.map {
            it.labelReference ?: ""
        }
        
        //val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(context, android.R.layout.simple_list_item_1, parcelRefList)

        this.uxParcelList.adapter = ParcelListAdapter(context, parcelList)
        //endregion
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

        /**
         * TODO: Action trigger on uxLabelNo TextView
         */
    }

    private fun processLabelRef(ref: String) {
        val order: Order? = stop.order.firstOrNull {
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
        view.setImageDrawable(resources.getDrawable(backgroundResource))
        view.visibility = View.VISIBLE

        resultCount++
    }

    private fun hideResultImages() {
        this.uxResultLeft.visibility = View.INVISIBLE
        this.uxResultRight.visibility = View.INVISIBLE
    }
}
