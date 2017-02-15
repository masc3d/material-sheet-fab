package org.deku.leoz.mobile.prototype.activities

import android.content.Context
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trello.rxlifecycle.components.support.RxAppCompatDialogFragment
import kotlinx.android.synthetic.main.fragment_proto_status.*
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Proto_StatusFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Proto_StatusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Proto_StatusFragment : RxAppCompatDialogFragment() {

    val log by lazy { LoggerFactory.getLogger(this.javaClass) }
    // TODO: Rename and change types of parameters
    private var mWiFiManager: WifiManager? = null
    private var mHandler: Handler? = Handler()
    var mRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mRunnable = Runnable {
            updateStatusBox()
            mHandler!!.postDelayed(mRunnable, 500)
        }

        mWiFiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager

    }



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_proto_status, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        mHandler!!.removeCallbacks(mRunnable)
    }

    private fun updateStatusBox(){
        if(mWiFiManager!!.isWifiEnabled){
            var mWiFiInfo : WifiInfo = mWiFiManager!!.connectionInfo
            if(mWiFiInfo.supplicantState == SupplicantState.COMPLETED){
                lblWifiStatus.text = "WiFi: ${WifiManager.calculateSignalLevel(mWiFiInfo.rssi, 5)}/5"
            }else{
                log.debug("WiFi not connected")
                lblWifiStatus.text = "Wifi: not connected"
            }
        }else{
            log.debug("WiFi not enabled")
            lblWifiStatus.text = "WiFi: N/A"
        }
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.


         * @return A new instance of fragment Proto_StatusFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): Proto_StatusFragment {
            val fragment = Proto_StatusFragment()
            return fragment
        }
    }
}// Required empty public constructor
