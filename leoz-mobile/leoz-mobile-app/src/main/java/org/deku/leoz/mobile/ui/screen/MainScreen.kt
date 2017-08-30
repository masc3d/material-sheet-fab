package org.deku.leoz.mobile.ui.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.screen_main.*
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.DebugSettings
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.process.Login
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.fragment.LoginFragment
import org.deku.leoz.mobile.ui.extension.inflateMenu
import org.deku.leoz.mobile.ui.view.ActionItem
import sx.android.fragment.util.withTransaction

/**
 * Main screen
 * Created by n3 on 26/02/2017.
 */
class MainScreen : ScreenFragment<Any>() {

    private val debugSettings: DebugSettings by Kodein.global.lazy.instance()

    val loginFragment: LoginFragment
        get() {
            return this.childFragmentManager.findFragmentByTag(
                    LoginFragment::class.java.canonicalName) as LoginFragment
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.title = "mobileX"
        this.headerImage = R.drawable.img_street_1a
    }

    /**
     * Create view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.screen_main, container, false)
        return rootView
    }

    /**
     * View created
     */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup views
        this.uxVersion.text = "v${BuildConfig.VERSION_NAME}"

        this.childFragmentManager.withTransaction {
            it.replace(this.uxContainer.id, LoginFragment(), LoginFragment::class.java.canonicalName)
        }

        if (this.debugSettings.enabled) {
            this.actionItems = listOf(
                    ActionItem(
                            id = R.id.action_dev,
                            colorRes = R.color.colorDev,
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