package org.deku.leoz.mobile.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.app
import org.deku.leoz.mobile.device.Tones
import org.deku.leoz.mobile.model.process.Login
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.service.UpdateService
import org.deku.leoz.mobile.ui.Activity
import org.deku.leoz.mobile.ui.fragment.LoginFragment
import org.deku.leoz.mobile.ui.screen.MainScreen
import sx.android.rx.observeOnMainThread

class MainActivity
    :
        Activity(),
        LoginFragment.Listener {

    private val login: Login by Kodein.global.lazy.instance()
    private val tones: Tones by Kodein.global.lazy.instance()

    private val db: Database by Kodein.global.lazy.instance()
    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()

    private val updateService: UpdateService by Kodein.global.lazy.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            this.supportActionBar?.title = getText(R.string.login)

            this.showScreen(
                    MainScreen(),
                    addToBackStack = false
            )

            if (this.login.authenticatedUser != null) {
                this.onLoginSuccessful()
            }
        }
    }

    override fun onBackPressed() {
        if (this.supportFragmentManager.backStackEntryCount == 0) {
            showExitDialog()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        this.loginPendingDialog.cancel()
        super.onDestroy()
    }

    private fun showExitDialog() {
        MaterialDialog.Builder(this)
                .title(R.string.exit_application)
                .content(R.string.exit_application_prompt)
                .positiveText(android.R.string.yes)
                .onPositive { _, _ -> this.app.terminate() }
                .negativeText(android.R.string.no)
                .build().show()
    }

    val loginPendingDialog by lazy {
        MaterialDialog.Builder(this)
                .title(R.string.login_pending)
                .content(R.string.please_wait)
                .cancelable(false)
                .progress(true, 0)
                .build()
    }

    override fun onResume() {
        super.onResume()

        // Disable login when updates become available
        this.updateService.availableUpdateProperty
                .filter { it.value != null }
                .map { it.value }
                .switchMap { update ->
                    // ..under specific conditions
                    Observable.combineLatest(
                            // has outdated orders
                            this.orderRepository.hasOutdatedOrders()
                                    .subscribeOn(db.scheduler)
                                    .toObservable(),
                            // No orders
                            this.orderRepository.entitiesProperty.map {
                                it.value.count() == 0
                            },
                            BiFunction { hasOutdatedOrders: Boolean, hasNoOrders: Boolean ->
                                hasOutdatedOrders || hasNoOrders
                            }
                    )
                            .filter { it == true }
                            .map { update }

                }
                .bindUntilEvent(this, ActivityEvent.PAUSE)
                .observeOnMainThread()
                .subscribe {
                    it.apk.install(this)
                }
    }

    //region LoginFragment listener
    override fun onLoginSuccessful() {
        this.startActivity(
                Intent(applicationContext, DeliveryActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

        this.finish()
    }

    override fun onLoginPending() {
        this.loginPendingDialog.show()
    }

    override fun onLoginFailed() {
        this.loginPendingDialog.dismiss()

        tones.errorBeep()

        this.snackbarBuilder
                .message(R.string.authentication_failed)
                .duration(Snackbar.LENGTH_SHORT)
                .build()
                .show()
    }
    //endregion
}