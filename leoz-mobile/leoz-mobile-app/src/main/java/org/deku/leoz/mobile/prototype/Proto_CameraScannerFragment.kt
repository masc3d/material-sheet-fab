package org.deku.leoz.mobile.prototype

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import kotlinx.android.synthetic.main.fragment_proto__camera_scanner.*
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory

class Proto_CameraScannerFragment : Fragment(), BarcodeCallback {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    private var listener: OnBarcodeResultListener? = null
    private val barcodeView: CompoundBarcodeView by lazy { uxBarcodeCamera }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proto__camera_scanner, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        barcodeView.isSoundEffectsEnabled = true
        barcodeView.playSoundEffect(SoundEffectConstants.CLICK)
        barcodeView.decodeContinuous(this)
    }

    override fun barcodeResult(result: BarcodeResult?) {
        if (result != null)
            onBarcodeScanned(result)
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
        //throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        log.debug("onPossibleResultPoints")
    }

    fun onBarcodeScanned(result: BarcodeResult) {
        val content: String = result.text
        if (listener != null) {
            listener!!.onBarcodeResult(content)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnBarcodeResultListener) {
            listener = context as OnBarcodeResultListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnBarcodeResultListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        barcodeView.pause()
        listener = null
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
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
    interface OnBarcodeResultListener {
        // TODO: Update argument type and name
        fun onBarcodeResult(content: String)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @param param1 Parameter 1.
         * *
         * @param param2 Parameter 2.
         * *
         * @return A new instance of fragment Proto_CameraScannerFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): Proto_CameraScannerFragment {
            val fragment = Proto_CameraScannerFragment()
            return fragment
        }
    }
}// Required empty public constructor
