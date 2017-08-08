package org.deku.leoz.mobile.prototype.activities.smallsort

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.properties.Bag
import org.deku.leoz.mobile.ui.Fragment
import org.slf4j.LoggerFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.proto_fragment_sso_outgoing.*
import sx.android.aidc.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProtoSsoOutgoingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProtoSsoOutgoingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProtoSsoOutgoingFragment : Fragment<Any>() {

    private var listener: OnFragmentInteractionListener? = null
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()

    private val scanMap: HashMap<Int, String> = HashMap()
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.proto_fragment_sso_outgoing, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val parent = this.parentFragment
        if (parent is OnFragmentInteractionListener) {
            listener = parent as OnFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onResume() {
        super.onResume()

        aidcReader.decoders.set(
                Interleaved25Decoder(true, 11, 12),
                DatamatrixDecoder(true),
                Ean8Decoder(true),
                Ean13Decoder(true)
        )

        aidcReader.readEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    this.processBarcodeData(it.data)
                }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @return A new instance of fragment ProtoSsoOutgoingFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): ProtoSsoOutgoingFragment {
            val fragment = ProtoSsoOutgoingFragment()
            return fragment
        }
    }

    private fun processBarcodeData(content: String) {
        // update UI to reflect the data
        val barcodeText = content

        activity.uxSSOOutStatus.text = ""
        clearStatusImage()
        //((TextView) findViewById(R.id.txtBagPkst)).setText(barcodeText);
        if (barcodeText.startsWith("10071")) {
            //Order-no
            activity.uxSSOOutOrderIDText.setText(barcodeText)
            scanMap.put(Bag.BAG_ORDERNO_HUB2STATION, barcodeText)
        }
        if (barcodeText.startsWith("10072")) {
            //Order-no
            setNOk()
            activity.uxSSOOutStatus.text = getString(R.string.hint_scan_label_upper_bc)
            activity.uxSSOOutOrderIDText.setText("")
            scanMap.remove(Bag.BAG_ORDERNO_HUB2STATION)
        }
        if (barcodeText.startsWith("9001")) {
            //White lead seal
            scanMap.put(Bag.LEADSEAL_WHITE, barcodeText)
            activity.uxSSOOutSealText.setText(barcodeText)
        }
        if (barcodeText.startsWith("9002")) {
            //Yellow lead seal
            scanMap.put(Bag.LEADSEAL_YELLOW, barcodeText)
            setNOk()
            activity.uxSSOOutStatus.text = getString(R.string.hint_leadseal_white_not_yellow)
        }
        if (scanMap.containsKey(Bag.LEADSEAL_WHITE) && scanMap.containsKey(Bag.BAG_ORDERNO_HUB2STATION))
            closeBag(java.lang.Long.parseLong(scanMap[Bag.BAG_ORDERNO_HUB2STATION]), java.lang.Long.parseLong(scanMap[Bag.LEADSEAL_WHITE]))
    }

    private fun closeBag(bag_orderno: Long, bag_leadseal: Long): Boolean {

        /*    val socketAddr = InetSocketAddress("", 8080)
            val proxy = Proxy(Proxy.Type.HTTP, socketAddr)
            val _wsSMS = CISwssmsSoap()
            val status = ""
            val farbe = ""
            val depot = ""
            try {
                val _response = _wsSMS.SackAusgang(2159, bag_orderno.toString(), bag_leadseal.toString(), status, farbe, depot)
                if (_response.sStatus !== "")
                    (findViewById(R.id.lblStatus) as TextView).setText(_response.sStatus)
                when (_response.sFarbe) {
                    "rot" -> setNOk()
                    "gelb" -> setWOk()
                    "gruen" -> setOk()
                }
                (findViewById(R.id.lblDepotNr) as TextView).setText(_response.sDepot)
                clearVariables()
            } catch (e: Exception) {
                (findViewById(R.id.lblStatus) as TextView).text = e.message
                clearVariables()
            }
        */
        return false
    }

    private fun setOk() {
        val image = activity.uxSSOOutStatusImage
        image.visibility = View.VISIBLE
        image.setImageDrawable(ContextCompat.getDrawable(activity.applicationContext, R.drawable.green))
    }

    private fun setWOk() {
        val image = activity.uxSSOOutStatusImage
        image.visibility = View.VISIBLE
        image.setImageDrawable(ContextCompat.getDrawable(activity.applicationContext, R.drawable.red))
    }

    private fun setNOk() {
        val image = activity.uxSSOOutStatusImage
        image.visibility = View.VISIBLE
        image.setImageDrawable(ContextCompat.getDrawable(activity.applicationContext, R.drawable.red))
    }

    private fun clearVariables() {
        activity.uxSSOOutOrderIDText.setText("")
        activity.uxSSOOutSealText.setText("")
        scanMap.clear()
    }

    private fun clearAll() {
        activity.uxSSOOutOrderIDText.setText("")
        activity.uxSSOOutSealText.setText("")
        activity.uxSSOOutStatusImage.visibility = View.INVISIBLE
        scanMap.clear()
    }

    private fun clearStatusImage() {
        activity.uxSSOOutStatusImage.visibility = View.INVISIBLE
    }
}
