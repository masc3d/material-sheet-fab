package org.deku.leoz.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.app
import org.deku.leoz.mobile.device.Feedback
import org.deku.leoz.mobile.model.entity.User
import org.deku.leoz.mobile.model.process.Login
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.service.UpdateService
import org.deku.leoz.mobile.ui.core.Activity
import org.deku.leoz.mobile.ui.process.LoginFragment
import org.deku.leoz.mobile.ui.process.MainScreen
import org.deku.leoz.model.VehicleType
import sx.android.rx.observeOnMainThreadUntilEvent

class MainActivity
    :
        Activity(),
        LoginFragment.Listener {

    private val login: Login by Kodein.global.lazy.instance()
    private val feedback: Feedback by Kodein.global.lazy.instance()

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

            this.login.authenticatedUser?.also {
                this.onLoginSuccessful(
                        it,
                        it.vehicleType)
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
                .observeOnMainThreadUntilEvent(this, ActivityEvent.PAUSE)
                .subscribe {
                    it.apk.install(this)
                }
    }

    //region LoginFragment listener
    override fun onLoginSuccessful(user: User, vehicleType: VehicleType) {
        user.vehicleType = vehicleType
        db.store.update(user).blockingGet()

        this.startActivity(
                Intent(applicationContext, TourActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

        this.finish()
    }

    override fun onLoginPending() {
        this.loginPendingDialog.show()
    }

    override fun onLoginFailed() {
        this.loginPendingDialog.dismiss()

        feedback.error()

        this.snackbarBuilder
                .message(R.string.authentication_failed)
                .duration(Snackbar.LENGTH_SHORT)
                .build()
                .show()
    }

    override fun onPrivacyRejected() {
        feedback.error()

        MaterialDialog.Builder(this).also {
            it.title("Data policy rejected")
            it.content("This service / app can not be used without accepting the data protection policy. The app will close now.")
            it.neutralText(R.string.dismiss)
            it.onNeutral { _, _ ->
                this.app.terminate()
            }
        }.show()
    }
    //endregion
}