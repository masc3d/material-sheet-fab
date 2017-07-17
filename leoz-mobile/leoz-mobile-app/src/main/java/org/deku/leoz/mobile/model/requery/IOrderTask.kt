package org.deku.leoz.mobile.model.requery

import android.databinding.Observable
import io.requery.*
import io.requery.query.Result
import org.deku.leoz.model.ParcelService
import java.util.*

/**
 * Order task entity
 * Created by masc on 16.07.17.
 */
@Entity(name = "OrderTaskEntity")
@Table(name = "order_task")
interface IOrderTask : Persistable, Observable {
    @get:Key @get:Generated
    var id: Int
    @get:ForeignKey @get:OneToOne
    var address: IAddress
    var dateStart: Date
    var dateEnd: Date
    var notBeforeStart: Boolean
    var notice: String

    @get:Convert(ServiceConverter::class)
    var services: ArrayList<ParcelService>
}