package org.deku.leoz.mobile.prototype.activities.smallsort

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.honeywell.aidc.*

import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.activities.Proto_MainActivity
import org.deku.leoz.mobile.prototype.properties.Bag
import org.slf4j.LoggerFactory
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Proto_sso_OutgoingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Proto_sso_OutgoingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Proto_sso_OutgoingFragment : Fragment(), BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener {

    private var mListener: OnFragmentInteractionListener? = null
    val barcodeReader: BarcodeReader = Proto_MainActivity.barcodeReader!!
    val scanMap: HashMap<Int, String> = HashMap()
    val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        acquireBarcodeReader()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_proto_sso__outgoing, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context as OnFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }

        try {
            barcodeReader.claim()
        } catch (e: ScannerUnavailableException) {
            e.printStackTrace()
            log.error("Scanner unavailable")
        }

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
        barcodeReader.release()
        barcodeReader.removeBarcodeListener(this)
        barcodeReader.removeTriggerListener(this)
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

    override fun onFailureEvent(p0: BarcodeFailureEvent?) {
        Snackbar.make(view!!, getString(R.string.error_imager_scan_failed), Snackbar.LENGTH_SHORT).show()
    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent) {
        activity.runOnUiThread(object: Runnable {
            override fun run() {
                // update UI to reflect the data
                val barcodeText = p0.barcodeData
                val barcodeResult = java.lang.Long.parseLong(barcodeText)
                (activity.findViewById(R.id.lblStatus) as TextView).text = ""
                clearStatusImage()
                //((TextView) findViewById(R.id.txtBagPkst)).setText(barcodeText);
                if (barcodeText.startsWith("10071")) {
                    //Order-no
                    (activity.findViewById(R.id.txtBagPkst) as TextView).setText(barcodeText)
                    scanMap.put(Bag.BAG_ORDERNO_HUB2STATION, barcodeText)
                }
                if (barcodeText.startsWith("10072")) {
                    //Order-no
                    setNOk()
                    (activity.findViewById(R.id.lblStatus) as TextView).text = "Unterer Barcode falsch! Oben scannen!"
                    (activity.findViewById(R.id.txtBagPkst) as TextView).text = ""
                    scanMap.remove(Bag.BAG_ORDERNO_HUB2STATION)
                }
                if (barcodeText.startsWith("9001")) {
                    //White lead seal
                    scanMap.put(Bag.LEADSEAL_WHITE, barcodeText)
                    (activity.findViewById(R.id.txtBagSeal) as TextView).setText(barcodeText)
                }
                if (barcodeText.startsWith("9002")) {
                    //Yellow lead seal
                    scanMap.put(Bag.LEADSEAL_YELLOW, barcodeText)
                    setNOk()
                    (activity.findViewById(R.id.lblStatus) as TextView).text = "Gelbe Plombe falsch! Weiss scannen!"
                }
                if (scanMap.containsKey(Bag.LEADSEAL_WHITE) && scanMap.containsKey(Bag.BAG_ORDERNO_HUB2STATION))
                    closeBag(java.lang.Long.parseLong(scanMap[Bag.BAG_ORDERNO_HUB2STATION]), java.lang.Long.parseLong(scanMap[Bag.LEADSEAL_WHITE]))
            }
        })
    }

    private fun acquireBarcodeReader() {

        // register bar code event listener
        barcodeReader.addBarcodeListener(this)

        // set the trigger mode to client control
        try {
            barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                    BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL)
        } catch (e: UnsupportedPropertyException) {
            Toast.makeText(activity.applicationContext, "Failed to apply properties", Toast.LENGTH_SHORT).show()
        }

        val properties: HashMap<String, Any> = HashMap<String, Any>();
        // Set Symbologies On/Off
        properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, true)
        properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, false)
        properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, false)
        properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, false)
        properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true)
        properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, false)
        properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, false)
        properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false)
        properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false)
        properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false)
        // Set Max Code 39 barcode length
        properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_MINIMUM_LENGTH, 11)
        properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_MAXIMUM_LENGTH, 12)
        // Turn on center decoding
        properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, false)
        // Disable bad read response, handle in onFailureEvent
        properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true)
        // Apply the settings
        barcodeReader.setProperties(properties)


        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
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
        val image = activity.findViewById(R.id.imageView) as ImageView
        image.visibility = View.VISIBLE
        image.setImageDrawable(ContextCompat.getDrawable(activity.applicationContext, R.drawable.green))
    }

    private fun setWOk() {
        val image = activity.findViewById(R.id.imageView) as ImageView
        image.visibility = View.VISIBLE
        image.setImageDrawable(ContextCompat.getDrawable(activity.applicationContext, R.drawable.red))
    }

    private fun setNOk() {
        val image = activity.findViewById(R.id.imageView) as ImageView
        image.visibility = View.VISIBLE
        image.setImageDrawable(ContextCompat.getDrawable(activity.applicationContext, R.drawable.red))
    }

    private fun clearVariables() {
        (activity.findViewById(R.id.lblDepotNr) as TextView).text = ""
        (activity.findViewById(R.id.txtBagPkst) as TextView).text = ""
        (activity.findViewById(R.id.txtBagSeal) as TextView).text = ""
        scanMap.clear()
    }

    private fun clearAll() {
        (activity.findViewById(R.id.lblDepotNr) as TextView).text = ""
        (activity.findViewById(R.id.txtBagPkst) as TextView).text = ""
        (activity.findViewById(R.id.txtBagSeal) as TextView).text = ""
        (activity.findViewById(R.id.imageView) as ImageView).visibility = View.INVISIBLE
        scanMap.clear()
    }

    private fun clearStatusImage() {
        (activity.findViewById(R.id.imageView) as ImageView).visibility = View.INVISIBLE
    }
}
