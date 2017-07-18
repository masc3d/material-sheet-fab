package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import org.deku.leoz.model.Carrier
import org.deku.leoz.model.OrderClassification
import sx.android.databinding.BaseRxObservable

/**
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "`order`")
abstract class Order : BaseRxObservable(), Persistable, Observable {
    @get:Key @get:Generated
    abstract val id: Long
    abstract var carrier: Carrier
    abstract var referenceIDToExchangeOrderID: Long
    abstract var orderClassification: OrderClassification

    @get:ForeignKey @get:OneToOne
    abstract var pickupTask: OrderTask
    @get:ForeignKey @get:OneToOne
    abstract var deliveryTask: OrderTask

    @get:OneToMany
    abstract val parcels: List<Parcel>
}