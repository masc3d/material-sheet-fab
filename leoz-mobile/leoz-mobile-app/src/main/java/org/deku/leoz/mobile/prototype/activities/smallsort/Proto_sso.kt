package org.deku.leoz.mobile.prototype.activities.smallsort

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory

class Proto_sso : AppCompatActivity(), Proto_sso_Menue_Fragment.OnFragmentInteractionListener {

    val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proto_sso__menue_f)
    }

    override fun onFragmentInteraction(uri: Uri) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onButtonClicked(buttonID: Int) {
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
