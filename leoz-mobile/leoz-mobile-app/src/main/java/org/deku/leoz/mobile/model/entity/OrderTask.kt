package org.deku.leoz.mobile.model.entity

import android.databinding.Observable
import io.requery.*
import org.deku.leoz.mobile.model.entity.converter.ServiceConverter
import org.deku.leoz.model.ParcelService
import sx.android.databinding.BaseRxObservable
import java.util.*

/**
 * Created by masc on 18.07.17.
 */
@Entity
@Table(name = "order_task")
abstract class OrderTask : BaseRxObservable(), Persistable, Observable {
    @get:Key @get:Generated
    abstract val id: Int
    @get:ForeignKey @get:OneToOne
    abstract var address: Address
    abstract var dateStart: Date
    abstract var dateEnd: Date
    abstract var notBeforeStart: Boolean
    abstract var notice: String

    @get:Convert(ServiceConverter::class)
    abstract var services: ArrayList<ParcelService>
}