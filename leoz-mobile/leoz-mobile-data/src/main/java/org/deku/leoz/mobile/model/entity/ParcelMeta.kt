package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import org.deku.leoz.mobile.model.entity.Parcel
import sx.android.databinding.BaseRxObservable

/**
* Mobile parcel metadata entity
* Created by masc on 18.07.17.
*/
@Entity
@Table(name = "parcel_meta")
abstract class ParcelMeta : BaseRxObservable(), Persistable, Observable {
    companion object {}

    enum class DataType {
        CASH_SERVICE
    }

    @get:Key @get:Generated
    abstract val id: Long

    /** Meta info type */
    abstract var type: DataType

    /** Opaque meta data, json encoded */
    abstract var data: String

    @get:Lazy
    @get:ManyToOne(cascade = arrayOf(CascadeAction.SAVE, CascadeAction.DELETE))
    abstract var parcel: Parcel
}