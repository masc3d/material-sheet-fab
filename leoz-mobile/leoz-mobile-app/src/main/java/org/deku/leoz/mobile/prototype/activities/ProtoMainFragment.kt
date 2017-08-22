package org.deku.leoz.mobile.prototype.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.proto_fragment_main.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.prototype.activities.smallsort.ProtoSsoActivity
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ScreenFragment

/**
 * Created by n3 on 06/03/2017.
 */
class ProtoMainFragment : ScreenFragment<Any>() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.proto_fragment_main, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uxHubButton.setOnClickListener({
            Snackbar.make(view, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
        })
        uxSsoButton.setOnClickListener({
            val mIntent = Intent(this.context, ProtoSsoActivity::class.java)
            startActivity(mIntent)
        })
    }
}

