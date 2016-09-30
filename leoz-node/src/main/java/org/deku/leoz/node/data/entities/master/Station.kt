package org.deku.leoz.node.data.entities.master

import sx.io.serialization.Serializable

import javax.persistence.*
import java.sql.Timestamp

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_station")
@Serializable(uid = 0x1f6229f711c472L)
class Station {
    @Id
    var stationNr: Int? = null
    @Basic
    @Column(nullable = false)
    var timestamp: Timestamp? = null
    @Basic
    var address1: String? = null
    @Basic
    var address2: String? = null
    @Basic
    var country: String? = null
    @Basic
    var zip: String? = null
    @Basic
    var city: String? = null
    @Basic
    var street: String? = null
    @Basic
    var houseNr: String? = null
    @Basic
    var phone1: String? = null
    @Basic
    var phone2: String? = null
    @Basic
    var telefax: String? = null
    @Basic
    var mobile: String? = null
    @Basic
    var servicePhone1: String? = null
    @Basic
    var servicePhone2: String? = null
    @Basic
    var contactPerson1: String? = null
    @Basic
    var contactPerson2: String? = null
    @Basic
    var email: String? = null
    @Basic
    var webAddress: String? = null
    @Basic
    var strang: Int? = null
    @Basic
    var posLong: Double? = null
    @Basic
    var posLat: Double? = null
    @Basic
    var sector: String? = null
    @Basic
    var uStId: String? = null
    @Basic
    var billingAddress1: String? = null
    @Basic
    var billingAddress2: String? = null
    @Basic
    var billingCountry: String? = null
    @Basic
    var billingZip: String? = null
    @Basic
    var billingCity: String? = null
    @Basic
    var billingStreet: String? = null
    @Basic
    var billingHouseNr: String? = null
    @Basic
    @Column(nullable = false)
    var syncId: Long? = null
}
