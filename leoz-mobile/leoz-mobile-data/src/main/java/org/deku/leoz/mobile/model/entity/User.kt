package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import org.deku.leoz.model.VehicleType
import sx.android.databinding.BaseRxObservable

/**
 * Mobile user entity
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "user_")
abstract class User : BaseRxObservable(), Persistable, Observable {

    companion object

    @get:Key
    abstract var id: Int
    @get:Column(nullable = false)
    abstract var email: String
    @get:Column(nullable = false)
    abstract var password: String
    @get:Column(nullable = false)
    abstract var apiKey: String
    /** The current user's vehicle type. Defaults to CAR */
    @get:Column(nullable = false)
    abstract var vehicleType: VehicleType
}

fun User.Companion.create(
        id: Int,
        email: String,
        password: String,
        apiKey: String,
        vehicleType: VehicleType = VehicleType.CAR
): UserEntity {
    return UserEntity().also {
        it.id = id
        it.email = email
        it.password = password
        it.apiKey = apiKey
        it.vehicleType = vehicleType
    }
}