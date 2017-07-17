package org.deku.leoz.mobile.model

import org.deku.leoz.mobile.model.requery.IParcel
import org.deku.leoz.mobile.model.requery.ParcelEntity

/**
 * Parcel
 */
data class Parcel constructor(
        val entity: ParcelEntity = ParcelEntity())
    : IParcel by entity {

    /** Passthrough c'tor */
    constructor(
            id: Int = 0,
            number: String,
            length: Double = 0.0,
            height: Double = 0.0,
            width: Double = 0.0,
            weight: Double = 0.0,
            state: Parcel.State = Parcel.State.PENDING
    )
            : this() {
        this.id = id
        this.number = number
        this.length = length
        this.height = height
        this.width = width
        this.weight = weight
        this.state = state
    }

    enum class State {
        PENDING, LOADED, MISSING, DONE, FAILED
    }
}