package org.deku.leoz.mobile.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.trello.rxlifecycle.components.support.RxAppCompatDialogFragment
import kotlinx.android.synthetic.main.fragment_login.*
import org.deku.leoz.mobile.R
import sx.android.Device

/**
 * Created by n3 on 26/02/2017.
 */
class LoginFragment : RxAppCompatDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val device: Device = Kodein.global.instance()
        this.uxSerialnumber.text = device.serial
    }
}