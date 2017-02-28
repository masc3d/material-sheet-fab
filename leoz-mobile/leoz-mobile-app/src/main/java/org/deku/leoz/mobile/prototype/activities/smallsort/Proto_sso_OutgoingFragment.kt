package org.deku.leoz.mobile.prototype.activities.smallsort

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.instanceOrNull
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle.components.support.RxAppCompatDialogFragment
import com.trello.rxlifecycle.kotlin.bindToLifecycle
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.Proto_CameraScannerFragment
import org.deku.leoz.mobile.prototype.properties.Bag
import org.slf4j.LoggerFactory
import rx.android.schedulers.AndroidSchedulers
import sx.android.aidc.BarcodeReader
import sx.android.aidc.DatamatrixDecoder
import sx.android.aidc.Ean8Decoder
import sx.android.aidc.Interleaved25Decoder
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Proto_sso_OutgoingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Proto_sso_OutgoingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Proto_sso_OutgoingFragment : RxAppCompatDialogFragment(), Proto_CameraScannerFragment.OnBarcodeResultListener {

    private var listener: OnFragmentInteractionListener? = null
    private val barcodeReader: BarcodeReader? by Kodein.global.lazy.instanceOrNull()

    private val scanMap: HashMap<Int, String> = HashMap()
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (barcodeReader == null) {
            initCameraScanner()
        }
        return inflater!!.inflate(R.layout.fragment_proto_sso__outgoing, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context as OnFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }

        this.barcodeReader
                ?.lifecycle
                ?.bindToLifecycle(this)

        this.barcodeReader?.decoders?.set(
                // TODO. min/max not supported just yet
                Interleaved25Decoder(true, 11, 12),
                DatamatrixDecoder(true),
                Ean8Decoder(true)
        )

        this.barcodeReader
                ?.readEvent
                ?.bindToLifecycle(this)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe {
                    this.processBarcodeData(it.data)
                }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
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

         * @return A new instance of fragment Proto_sso_OutgoingFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): Proto_sso_OutgoingFragment {
            val fragment = Proto_sso_OutgoingFragment()
            return fragment
        }
    }

    private fun processBarcodeData(content: String) {
        // update UI to reflect the data
        val barcodeText = content
        (activity.findViewById(R.id.uxSSOOutStatus) as TextView).text = ""
        clearStatusImage()
        //((TextView) findViewById(R.id.txtBagPkst)).setText(barcodeText);
        if (barcodeText.startsWith("10071")) {
            //Order-no
            (activity.findViewById(R.id.uxSSOOutOrderIDText) as TextView).setText(barcodeText)
            scanMap.put(Bag.BAG_ORDERNO_HUB2STATION, barcodeText)
        }
        if (barcodeText.startsWith("10072")) {
            //Order-no
            setNOk()
            (activity.findViewById(R.id.uxSSOOutStatus) as TextView).text = getString(R.string.hint_scan_label_upper_bc)
            (activity.findViewById(R.id.uxSSOOutOrderIDText) as TextView).text = ""
            scanMap.remove(Bag.BAG_ORDERNO_HUB2STATION)
        }
        if (barcodeText.startsWith("9001")) {
            //White lead seal
            scanMap.put(Bag.LEADSEAL_WHITE, barcodeText)
            (activity.findViewById(R.id.uxSSOOutSealText) as TextView).setText(barcodeText)
        }
        if (barcodeText.startsWith("9002")) {
            //Yellow lead seal
            scanMap.put(Bag.LEADSEAL_YELLOW, barcodeText)
            setNOk()
            (activity.findViewById(R.id.uxSSOOutStatus) as TextView).text = getString(R.string.hint_leadseal_white_not_yellow)
        }
        if (scanMap.containsKey(Bag.LEADSEAL_WHITE) && scanMap.containsKey(Bag.BAG_ORDERNO_HUB2STATION))
            closeBag(java.lang.Long.parseLong(scanMap[Bag.BAG_ORDERNO_HUB2STATION]), java.lang.Long.parseLong(scanMap[Bag.LEADSEAL_WHITE]))
    }

    override fun onBarcodeResult(content: String) {
        activity.runOnUiThread(object : Runnable {
            override fun run() {
                processBarcodeData(content)
            }
        })
        //throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun initCameraScanner() {
        val mCameraScannerFragment: Proto_CameraScannerFragment = Proto_CameraScannerFragment.newInstance()
        val mTransaction: FragmentTransaction = childFragmentManager.beginTransaction()
        mTransaction.add(R.id.uxSSOOutCamFragment, mCameraScannerFragment)
        mTransaction.commit()
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
        val image = activity.findViewById(R.id.uxSSOOutStatusImage) as ImageView
        image.visibility = View.VISIBLE
        image.setImageDrawable(ContextCompat.getDrawable(activity.applicationContext, R.drawable.green))
    }

    private fun setWOk() {
        val image = activity.findViewById(R.id.uxSSOOutStatusImage) as ImageView
        image.visibility = View.VISIBLE
        image.setImageDrawable(ContextCompat.getDrawable(activity.applicationContext, R.drawable.red))
    }

    private fun setNOk() {
        val image = activity.findViewById(R.id.uxSSOOutStatusImage) as ImageView
        image.visibility = View.VISIBLE
        image.setImageDrawable(ContextCompat.getDrawable(activity.applicationContext, R.drawable.red))
    }

    private fun clearVariables() {
        (activity.findViewById(R.id.uxSSOOutOrderIDText) as TextView).text = ""
        (activity.findViewById(R.id.uxSSOOutSealText) as TextView).text = ""
        scanMap.clear()
    }

    private fun clearAll() {
        (activity.findViewById(R.id.uxSSOOutOrderIDText) as TextView).text = ""
        (activity.findViewById(R.id.uxSSOOutSealText) as TextView).text = ""
        (activity.findViewById(R.id.uxSSOOutStatusImage) as ImageView).visibility = View.INVISIBLE
        scanMap.clear()
    }

    private fun clearStatusImage() {
        (activity.findViewById(R.id.uxSSOOutStatusImage) as ImageView).visibility = View.INVISIBLE
    }
}
