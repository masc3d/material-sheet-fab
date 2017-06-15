package org.deku.leoz.mobile.ui.screen

import android.os.Bundle
import android.support.v7.view.menu.MenuBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.fragment_main.*
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.DebugSettings
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.Login
import org.deku.leoz.mobile.ui.Fragment
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.fragment.LoginFragment
import org.deku.leoz.mobile.ui.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem

/**
 * Created by n3 on 26/02/2017.
 */
class MainScreen : ScreenFragment() {

    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()

    val loginFragment: LoginFragment
        get() {
            return this.childFragmentManager.findFragmentByTag(
                    LoginFragment::class.java.canonicalName) as LoginFragment
        }

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
        ft.replace(this.uxContainer.id, LoginFragment(), LoginFragment::class.java.canonicalName)
        ft.commit()

        if (this.debugSettings.enabled) {
            this.actionItems = listOf(
                    ActionItem(
                            id = R.id.action_dev,
                            colorRes = R.color.colorPrimary,
                            iconRes = R.drawable.ic_dev,
                            iconTintRes = android.R.color.white,
                            menu = this.activity.inflateMenu(R.menu.menu_dev)
                    )
            )
        }
    }

    override fun onResume() {
        super.onResume()

        this.activity.actionEvent
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    when (it) {
                        R.id.action_dev_login -> {
                            this.loginFragment.synthesizeLogin(
                                    email = Login.DEV_EMAIL,
                                    password = Login.DEV_PASSWORD
                            )
                        }
                    }
                }
    }
}