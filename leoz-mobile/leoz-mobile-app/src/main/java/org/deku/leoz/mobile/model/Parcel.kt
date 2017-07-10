package org.deku.leoz.mobile.model

import org.deku.leoz.mobile.data.requery.IParcelEntity
import org.deku.leoz.mobile.data.requery.ParcelEntity

/**
 * Mobile parcel
 */
data class Parcel constructor(
        val entity: ParcelEntity = ParcelEntity()) : IParcelEntity by entity {

    /** Passthrough c'tor */
    constructor(
            id: String,
            labelRef: String,
            length: Double = 0.0,
            height: Double = 0.0,
            width: Double = 0.0,
            weight: Double = 0.0)
            : this() {
        this.id = id
        this.labelRef = labelRef
        this.length = length
        this.height = height
        this.width = width
        this.weight = weight
    }

    var state: Parcel.State = Parcel.State.PENDING
    val status: MutableList<Order.Status> = mutableListOf()

    enum class State {
        PENDING, LOADED, MISSING, DONE, FAILED
    }

    init {
    }
}