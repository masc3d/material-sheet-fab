package org.deku.leoz.mobile.ui.screen

import android.os.Bundle
import android.support.v7.view.menu.MenuBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.fragment.LoginFragment
import org.deku.leoz.mobile.ui.view.ActionItem

/**
 * Created by n3 on 26/02/2017.
 */
class MainScreen : ScreenFragment() {

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

        val helpMenu = MenuBuilder(this.context)
        this.activity.menuInflater.inflate(R.menu.menu_help, helpMenu)

        this.actionItems = listOf(
                ActionItem(
                        id = R.id.action_help,
                        colorRes = R.color.colorPrimary,
                        iconRes = android.R.drawable.ic_menu_help,
                        menu = helpMenu
                ),
                ActionItem(
                        id = R.id.action_help,
                        colorRes = R.color.colorPrimary,
                        iconRes = android.R.drawable.ic_menu_help,
                        menu = helpMenu
                )
        )
    }
}