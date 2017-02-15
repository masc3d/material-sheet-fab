package org.deku.leoz.mobile.prototype.activities.smallsort

import android.support.v4.app.FragmentTransaction
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_proto_sso.*
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory

class Proto_sso : AppCompatActivity(), Proto_sso_MenueFragment.OnFragmentInteractionListener, Proto_sso_OutgoingFragment.OnFragmentInteractionListener {

    val log by lazy { LoggerFactory.getLogger(this.javaClass) }

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
        if(savedInstanceState == null) {
            createMenueFragment()
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createMenueFragment(){
        val mFragmentMenue: Proto_sso_MenueFragment = Proto_sso_MenueFragment.newInstance()
        val mFragmentTransaction: FragmentTransaction? = supportFragmentManager.beginTransaction()
        mFragmentTransaction!!.add(uxSSOFragmentContainer.id, mFragmentMenue)
        mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }

    override fun onButtonClicked(buttonID: Int) {
        log.debug("onButtonClicked")
        if(findViewById(buttonID) != null){
            if(findViewById(buttonID) is Button || findViewById(buttonID) is ImageButton){
                when(buttonID){
                    R.id.btnBagDifference -> {

                    }
                    R.id.btnBagIncoming -> {

                    }
                    R.id.btnBagInitialize -> {

                    }
                    R.id.btnBagOutgoing -> {
                        //TODO Switch Fragment to Bag-Outgoing Process
                        val mFragmentSSOOutgoing: Proto_sso_OutgoingFragment = Proto_sso_OutgoingFragment()
                        val mFragmentTransaction: FragmentTransaction? = supportFragmentManager.beginTransaction()
                        mFragmentTransaction!!.replace(uxSSOFragmentContainer.id, mFragmentSSOOutgoing)
                        mFragmentTransaction.addToBackStack(null)
                        mFragmentTransaction.commit()
                    }
                    R.id.btnBagSort -> {

                    }
                    else -> {
                        log.debug("OnClick Event unhandled [${findViewById(buttonID).rootView}]")
                        Snackbar.make(findViewById(buttonID).rootView, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            log.debug("Fragment onButtonClicked event for non-button control [${buttonID}]")
        }
    }
}
