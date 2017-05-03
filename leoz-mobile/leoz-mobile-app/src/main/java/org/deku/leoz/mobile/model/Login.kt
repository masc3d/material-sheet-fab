package org.deku.leoz.mobile.model

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.service.internal.AuthorizationService
import sx.android.Connectivity
import sx.rx.ObservableRxProperty

/**
 * Login model
 * Created by n3 on 27.04.17.
 */
class Login {
    private val connectivity: Connectivity by Kodein.global.lazy.instance()

    // Consumers can observe this property for changes
    val authenticationResponseProperty = ObservableRxProperty<AuthorizationService.MobileResponse>(AuthorizationService.MobileResponse(""))
    // Delegated property for convenient access
    var authenticationResponse: AuthorizationService.MobileResponse by authenticationResponseProperty

    private val authService: AuthorizationService by Kodein.global.lazy.instance()

    /**
     * Authenticate user (asnychronously)
     * @param email User email
     * @param password User password
     */
    fun authenticate(authRequest: AuthorizationService.MobileRequest): Observable<AuthorizationService.MobileResponse> {
        val task = Observable.fromCallable {
            val authResponse: AuthorizationService.MobileResponse

            if(connectivity.isEstablished) {
                authResponse = authService.authorizeMobile(authRequest)
                //TODO: Store/Update local credentials in DB
            } else {
                //TODO: check request against local stored credentials
                authResponse = AuthorizationService.MobileResponse("LOCAL_API-KEY_HERE")
            }

            authResponse
        }
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        // Subscribing to task will actually start it
        task.subscribe {
            // Store authenticated user in property
            this.authenticationResponse = it
        }

        // Return task to consumer for optionally subscribing to running authentication task as well
        return task
    }
}