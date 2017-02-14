package org.deku.leoz.mobile.prototype.activities.smallsort

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.activities.smallsort.Proto_sso_Menue_Fragment.OnFragmentInteractionListener

class Proto_sso_Menue_f : AppCompatActivity(), OnFragmentInteractionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proto_sso__menue_f)
    }

    override fun onFragmentInteraction(uri: Uri) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onButtonClicked(buttonID: Int) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
