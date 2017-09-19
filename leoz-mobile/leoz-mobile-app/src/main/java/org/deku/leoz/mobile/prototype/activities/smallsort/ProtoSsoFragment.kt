package org.deku.leoz.mobile.prototype.activities.smallsort

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import kotlinx.android.synthetic.main.proto_fragment_sso.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ScreenFragment
import org.slf4j.LoggerFactory

/**
* Created by masc on 06/03/2017.
*/
class ProtoSsoFragment
    :
        ScreenFragment<Any>(),
        ProtoSsoMenuFragment.OnFragmentInteractionListener,
        ProtoSsoOutgoingFragment.OnFragmentInteractionListener  {

    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.proto_fragment_sso, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createMenueFragment()
    }

    override fun onFragmentInteraction(uri: Uri) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createMenueFragment() {
        val fragmentMenu: ProtoSsoMenuFragment = ProtoSsoMenuFragment.newInstance()
        val fragmentTransaction: FragmentTransaction? = childFragmentManager.beginTransaction()
        fragmentTransaction!!.add(uxSsoFragmentContainer.id, fragmentMenu)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onButtonClicked(buttonID: Int) {
        log.debug("onButtonClicked")
        val buttonView = this.view?.findViewById<View>(buttonID)
        if (buttonView != null) {
            if (buttonView is Button || buttonView is ImageButton) {
                when (buttonID) {
                    R.id.uxBagDifference -> {

                    }
                    R.id.uxBagIncoming -> {

                    }
                    R.id.uxBagInitialize -> {

                    }
                    R.id.uxBagOutgoing -> {
                        //TODO Switch Fragment to Bag-Outgoing Process
                        val fragmentSsoOutgoing: ProtoSsoOutgoingFragment = ProtoSsoOutgoingFragment()
                        val fragmentTransaction: FragmentTransaction? = childFragmentManager.beginTransaction()
                        fragmentTransaction!!.replace(uxSsoFragmentContainer.id, fragmentSsoOutgoing)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    }
                    R.id.uxBagSort -> {

                    }
                    else -> {
                        log.debug("OnClick Event unhandled [${buttonView.rootView}]")
                        Snackbar.make(buttonView.rootView, getString(R.string.hint_not_available), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            log.debug("Fragment onButtonClicked event for non-button control [${buttonID}]")
        }
    }
}