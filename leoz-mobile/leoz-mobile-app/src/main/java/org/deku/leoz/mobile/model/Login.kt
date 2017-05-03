package org.deku.leoz.mobile.model

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.hashUserPassword
import org.deku.leoz.service.internal.AuthorizationService
import sx.android.Connectivity
import sx.android.Device
import sx.rx.ObservableRxProperty
import sx.text.parseHex

/**
 * Login model
 * Created by n3 on 27.04.17.
 */
class Login {
    private val connectivity: Connectivity by Kodein.global.lazy.instance()
    private val device: Device = Kodein.global.instance()
    private val authService: AuthorizationService by Kodein.global.lazy.instance()

    private val SALT = "".parseHex()

    // Consumers can observe this property for changes
    val authenticatedUserProperty = ObservableRxProperty<User?>(null)
    // Delegated property for convenient access
    var authenticatedUser: User? by authenticatedUserProperty

    /**
     * Authenticate user (asnychronously)
     * @param email User email
     * @param password User password
     */
    fun authenticate(email: String, password: String): Observable<User?> {
        val task = Observable.fromCallable {
            val authResponse: AuthorizationService.MobileResponse
            var user: User? = null

            if(connectivity.isEstablished) {
                val request = AuthorizationService.MobileRequest(
                        user = AuthorizationService.Credentials(
                                email = email,
                                password = password
                        ),
                        mobile = AuthorizationService.Mobile(
                                model = device.model.name,
                                serial = device.serial,
                                imei = device.imei
                        )
                )

                authResponse = authService.authorizeMobile(request)

                if (authResponse.key.isNotBlank()) {
                    user = User(
                            name = email,
                            hash = hashUserPassword(
                                    salt = SALT,
                                    email = email,
                                    password = password
                            )
                    )
                }
                //TODO: Store/Update local credentials in DB
            } else {
                //TODO: check request against local stored credentials
                user = User(
                        name = "OFFLINE_USER",
                        hash = "offline_hash"
                )
            }

            user
        }
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        // Subscribing to task will actually start it
        task.subscribe {
            // Store authenticated user in property
            this.authenticatedUser = it
        }

        // Return task to consumer for optionally subscribing to running authentication task as well
        return task
    }
}