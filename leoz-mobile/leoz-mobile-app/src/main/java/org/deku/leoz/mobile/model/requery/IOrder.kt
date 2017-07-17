package org.deku.leoz.mobile.model.requery

import android.databinding.Observable
import io.requery.*
import io.swagger.annotations.ApiModelProperty
import org.deku.leoz.model.Carrier
import org.deku.leoz.model.OrderClassification
import org.deku.leoz.service.internal.OrderService

/**
 * Order entity
 * Created by masc on 16.07.17.
 */
@Entity(name = "OrderEntity")
@Table(name = "`order`")
interface IOrder : Persistable, Observable {
    @get:Key @get:Generated
    var id: Long
    var carrier: Carrier
    var referenceIDToExchangeOrderID: Long
    // TODO: enum
    var orderClassification: Int

    @get:ForeignKey @get:OneToOne
    var pickupTask: IOrderTask
    @get:ForeignKey @get:OneToOne
    var deliveryTask: IOrderTask

    @get:OneToMany
    var parcels: Set<IParcel>
}