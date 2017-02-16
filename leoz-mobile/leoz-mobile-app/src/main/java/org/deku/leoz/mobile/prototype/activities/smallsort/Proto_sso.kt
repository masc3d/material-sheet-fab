package org.deku.leoz.mobile.prototype.activities.smallsort

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_proto_sso.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.Proto_CameraScannerFragment
import org.slf4j.LoggerFactory

class Proto_sso : AppCompatActivity(), Proto_sso_MenueFragment.OnFragmentInteractionListener, Proto_sso_OutgoingFragment.OnFragmentInteractionListener, Proto_CameraScannerFragment.OnBarcodeResultListener {

    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
//        val mBundle: Bundle? = savedInstanceState
//        try{
//            mBundle!!.remove("android:support:fragments")
//        }catch(e: Exception){
//
//        }
        super.onCreate(savedInstanceState)
        log.debug("onCreate")
        setContentView(R.layout.activity_proto_sso)
        if (savedInstanceState == null) {
            createMenueFragment()
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBarcodeResult(content: String) {
        //throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        log.debug("onBarcodeResult")
    }

    private fun createMenueFragment() {
        val fragmentMenu: Proto_sso_MenueFragment = Proto_sso_MenueFragment.newInstance()
        val fragmentTransaction: FragmentTransaction? = supportFragmentManager.beginTransaction()
        fragmentTransaction!!.add(uxSsoFragmentContainer.id, fragmentMenu)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onButtonClicked(buttonID: Int) {
        log.debug("onButtonClicked")
        if (findViewById(buttonID) != null) {
            if (findViewById(buttonID) is Button || findViewById(buttonID) is ImageButton) {
                when (buttonID) {
                    R.id.uxBagDifference -> {

                    }
                    R.id.uxBagIncoming -> {

                    }
                    R.id.uxBagInitialize -> {

                    }
                    R.id.uxBagOutgoing -> {
                        //TODO Switch Fragment to Bag-Outgoing Process
                        val fragmentSSOOutgoing: Proto_sso_OutgoingFragment = Proto_sso_OutgoingFragment()
                        val fragmentTransaction: FragmentTransaction? = supportFragmentManager.beginTransaction()
                        fragmentTransaction!!.replace(uxSsoFragmentContainer.id, fragmentSSOOutgoing)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    }
                    R.id.uxBagSort -> {

                    }
                    else -> {
                        log.debug("OnClick Event unhandled [${findViewById(buttonID).rootView}]")
                        Snackbar.make(findViewById(buttonID).rootView, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            log.debug("Fragment onButtonClicked event for non-button control [${buttonID}]")
        }
    }
}
