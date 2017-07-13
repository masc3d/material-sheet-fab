package org.deku.leoz.mobile.ui.fragment


import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_delivery_stop_process.*
import org.deku.leoz.mobile.BR

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Order
import org.deku.leoz.mobile.model.Parcel
import org.deku.leoz.mobile.model.Stop
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import org.deku.leoz.mobile.ui.vm.ParcelViewModel
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.android.aidc.AidcReader
import sx.android.ui.flexibleadapter.FlexibleVmSectionableItem


/**
 * A simple [Fragment] subclass.
 */
class StopProcessFragment : Fragment() {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()

    private lateinit var stop: Stop
    private val orderList: MutableList<Order> = mutableListOf()
    private val parcelList: MutableList<Parcel> = mutableListOf()
    private var lastRef: String? = null
    private var resultCount: Int = 0

    private val parcelListAdapterInstance = LazyInstance<
            FlexibleAdapter<
                    FlexibleVmSectionableItem<
                            ParcelViewModel>>>({

        val adapter = FlexibleAdapter(
                // Items
                stop.orders
                        .flatMap { it.parcel }
                        .map {
                            val item = FlexibleVmSectionableItem(
                                    viewRes = R.layout.item_parcel,
                                    variableId = BR.parcel,
                                    viewModel = ParcelViewModel(it)
                            )

                            item.isEnabled = true
                            item.isDraggable = true
                            item.isSwipeable = false

                            item
                        },
                // Listener
                this)

        adapter.setDisplayHeadersAtStartUp(true)
        adapter.setStickyHeaders(true)
        adapter.showAllHeaders()

        adapter
    })
    private val parcelListAdapter get() = parcelListAdapterInstance.get()

    companion object {
        /**
         * Create instance with parameters. This pattern requires `retainInstance` to be set in `onCreate`!
         */
        fun create(stop: Stop): StopProcessFragment {
            val f = StopProcessFragment()
            f.stop = stop
            f.orderList.addAll(f.stop.orders.filter { it.state == Order.State.LOADED })
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
            f.orderList.addAll(orders)
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

        // Flexible adapter needs to be re-created with views
        this.parcelListAdapterInstance.reset()

        this.uxParcelList.adapter = parcelListAdapter
        this.uxParcelList.layoutManager = LinearLayoutManager(context
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
                it.labelRef == ref
            } != null
        }

        hideResultImages()

        if (order != null) {
            showResult(R.drawable.green)
        } else {
            //Parcel is not part of this stop
            if (lastRef.isNullOrBlank() || lastRef != ref) {
                //No (similar) reference scanned previously
                showResult(R.drawable.red)
            } else {
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
