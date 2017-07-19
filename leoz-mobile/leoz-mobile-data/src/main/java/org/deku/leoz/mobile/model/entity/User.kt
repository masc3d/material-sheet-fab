package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import org.deku.leoz.model.VehicleType
import sx.android.databinding.BaseRxObservable

/**
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "`user`")
abstract class User : BaseRxObservable(), Persistable, Observable {

    companion object

    @get:Key
    abstract var id: Int
    abstract var email: String
    abstract var password: String
    abstract var apiKey: String
    /** The current user's vehicle type. Defaults to CAR */
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