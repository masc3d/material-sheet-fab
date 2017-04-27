package org.deku.leoz.mobile.model

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.rest.service.internal.v1.UserService
import org.apache.commons.codec.digest.DigestUtils
import sx.rx.ObservableRxProperty
import sx.rx.task
import java.util.concurrent.ExecutorService

/**
 * Login model
 * Created by n3 on 27.04.17.
 */
class Login {
    // Consumers can observe this property for changes
    val authenticatedUserProperty = ObservableRxProperty<User?>(null)
    // Delegated property for convenient access
    var authenticatedUser: User? by authenticatedUserProperty

    private val userService: UserService by Kodein.global.lazy.instance()

    /**
     * Authenticate user (asnychronously)
     * @param email User email
     * @param password User password
     */
    fun authenticate(email: String, password: String): Observable<User> {
        val task = Observable.fromCallable {
            // TODO: check if we're online, if not try offline auth against db

            val user = userService.get(email)

            // TODO: store user info in db

            // TODO: hash password, verify
            DigestUtils.sha256(password)

            User(
                    name = user.alias ?: "",
                    hash = user.password ?: "")
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