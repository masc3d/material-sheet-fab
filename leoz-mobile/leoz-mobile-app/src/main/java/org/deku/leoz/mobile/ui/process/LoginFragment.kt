package org.deku.leoz.mobile.ui.process

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.view_vehicletypes.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.entity.User
import org.deku.leoz.mobile.model.process.Login
import org.deku.leoz.mobile.ui.core.Fragment
import org.deku.leoz.model.VehicleType
import org.jetbrains.anko.inputMethodManager
import org.slf4j.LoggerFactory
import sx.android.view.hideSoftInput
import sx.rx.just
import java.util.concurrent.TimeUnit
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

/**
 * Login fragment
 * Created by n3 on 26/02/2017.
 */
class LoginFragment : Fragment<Any>() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val internalLoginRegex: Regex = Regex(pattern = "^276[0-9]{5}$")
    private val login: Login by Kodein.global.lazy.instance()

    interface Listener {
        /** Called when it's appropriate to show progress indication */
        fun onLoginPending() {}

        fun onLoginFailed() {}
        fun onLoginSuccessful(user: User) {}

        fun onPrivacyRejected() {}
    }

    private val listener by listenerDelegate<Listener>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.uxMailaddress.requestFocus()

        // On-the-fly validations

        RxView.focusChanges(this.uxMailaddress)
                .filter { it == false }
                .subscribe {
                    this.validateMailAddress()
                }

        // Clear error condition on typing

        RxTextView.textChanges(uxMailaddress).subscribe {
            this.uxMailaddressLayout.error = null
        }

        RxTextView.textChanges(uxPassword).subscribe {
            this.uxPasswordLayout.error = null
        }

        this.uxVehicleTypes.selected = this.uxVan
    }

    override fun onResume() {
        super.onResume()

        data class State(
                val pending: Boolean = false,
                val user: User? = null
        ) {
            val isComplete get() = this.pending == false
        }

        // Actions triggering login

        val rxLoginTrigger =
                Observable.merge(listOf(
                        RxTextView.editorActions(this.uxPassword)
                                .map { Unit }
                                .replay(1)
                                .refCount()
                                .doOnNext {
                                    this.context.inputMethodManager.hideSoftInput()
                                },
                        this.syntheticLoginSubject
                ))

        rxLoginTrigger
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap {
                    Observable.fromCallable {
                        // Verify all fields
                        if (listOf(
                                        validateMailAddress(),
                                        validatePassword()
                                ).any { it == false }) {
                            throw IllegalArgumentException("Validation failed")
                        }
                    }
                }
                .switchMap {
                    login.authenticate(
                            email = uxMailaddress.text.toString(),
                            password = uxPassword.text.toString()
                    )
                            .map {
                                // Map success result to state
                                State(pending = false, user = it)
                            }
                            // Merge delayed pending state
                            .mergeWith(Observable
                                    .just(State(pending = true))
                                    .delay(250, TimeUnit.MILLISECONDS)
                            )
                            // Complete this observable on success result
                            .takeUntil { state -> state.isComplete }
                }
                .switchMap { state ->
                    when {
                        state.isComplete -> this.queryPrivacyConfirmation()
                                .toObservable()
                                .switchMap {
                                    when (it) {
                                        true -> state.just()
                                        false -> Observable.empty()
                                    }
                                }
                        else -> state.just()
                    }
                }
                .doOnError {
                    log.error(it.message, it)
                    this.view?.post {
                        this.listener?.onLoginFailed()
                    }
                }
                // Retrying the entire observable (including required triggers, eg. user input)
                .retry()
                .bindUntilEvent(this, FragmentEvent.PAUSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state ->
                    when {
                        state.isComplete -> {
                            log.info("Login successful $state")

                            if (state.user == null) throw IllegalStateException()

                            state.user.vehicleType = when (this.uxVehicleTypes.selected) {
                                uxVan -> VehicleType.VAN
                                uxTruck -> VehicleType.TRUCK
                                uxBike -> VehicleType.BIKE
                                uxCar -> VehicleType.CAR
                                else -> throw IllegalArgumentException("Unsupported vehicle tpye")
                            }

                            this.listener?.onLoginSuccessful(
                                    user = state.user
                            )
                        }
                        else -> {
                            this.listener?.onLoginPending()
                        }
                    }
                }
    }

    private fun String.isValidEmailAddress(): Boolean {
        return try {
            InternetAddress(this).validate()
            true
        } catch (e: AddressException) {
            false
        }
    }

    private fun validateMailAddress(): Boolean = when {
        uxMailaddress.text.isEmpty() -> {
            this.uxMailaddressLayout.error = getString(R.string.error_empty_field)
            false
        }
        !uxMailaddress.text.toString().isValidEmailAddress() && !uxMailaddress.text.matches(internalLoginRegex) -> {
            this.uxMailaddressLayout.error = getString(R.string.error_format_mail)
            false
        }
        else -> {
            this.uxMailaddressLayout.error = null
            true
        }
    }

    private fun validatePassword(): Boolean = when {
        this.uxPassword.text.isEmpty() -> {
            this.uxPasswordLayout.error = getString(R.string.error_empty_field)
            false
        }
        else -> {
            this.uxPasswordLayout.error = null
            true
        }
    }

    private val syntheticLoginSubject = PublishSubject.create<Unit>()

    /**
     * Synthesizes login by adding a user/password into the referring field
     * and triggering the login process
     * @param email User mail address
     * @param password Password
     */
    fun synthesizeLogin(email: String, password: String) {
        this.uxMailaddress.setText(email)
        this.uxPassword.setText(password)
        this.syntheticLoginSubject.onNext(Unit)
    }

    /**
     * Displays query confirmation
     * @return privacy policy accepted or not
     */
    private fun queryPrivacyConfirmation(): Single<Boolean> {
        return Single.create<Boolean> { emitter ->

            var disclaimerDialog: MaterialDialog? = null

            disclaimerDialog = MaterialDialog.Builder(this.context).also {
                it.title(R.string.data_protection)
                it.icon(ContextCompat.getDrawable(this.context, R.drawable.ic_search_data)!!)
                it.checkBoxPromptRes(R.string.data_protection_accept, false, { _, checked ->
                    disclaimerDialog?.getActionButton(DialogAction.POSITIVE)?.isEnabled = checked
                })
                it.content(R.string.privacy_disclaimer_text)
                it.cancelable(false)
                it.positiveText(R.string.proceed)
                it.negativeText(R.string.cancel)
                it.onNegative { _, _ -> emitter.onSuccess(false) }
                it.onPositive { _, _ -> emitter.onSuccess(true) }
            }.build().also {
                it.getActionButton(DialogAction.POSITIVE).isEnabled = false
            }

            disclaimerDialog.show()
        }
                .subscribeOn(AndroidSchedulers.mainThread())
    }
}