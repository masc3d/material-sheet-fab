package org.deku.leoz.mobile.ui.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.fragment.LoginFragment

/**
 * Created by n3 on 26/02/2017.
 */
class MainFragment : Fragment() {

    /**
     * Create view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        return rootView
    }

    /**
     * View created
     */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup views
        this.uxVersion.text = "v${BuildConfig.VERSION_NAME}"

        val ft = this.childFragmentManager.beginTransaction()
        ft.replace(this.uxContainer.id, LoginFragment())
        ft.commit()
    }
}