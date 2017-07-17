package org.deku.leoz.mobile.model.requery

import android.databinding.Observable
import io.requery.*
import org.deku.leoz.model.VehicleType

/**
 * User entity
 * Created by masc on 16.07.17.
 */
@Entity(name = "UserEntity")
@Table(name = "user")
interface IUser : Persistable, Observable {
    @get:Key @get:Generated
    var id: Int
    var email: String
    var password: String
    var apiKey: String
    /** The current user's vehicle type. Defaults to CAR */
    var vehicleType: VehicleType
}