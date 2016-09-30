package org.deku.leoz.node.data.entities.master

import org.eclipse.persistence.annotations.Index
import org.eclipse.persistence.config.QueryHints
import sx.io.serialization.Serializable

import javax.persistence.*
import java.sql.Time
import java.sql.Timestamp

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_route", indexes = arrayOf(javax.persistence.Index(columnList = "layer, country, zipFrom, zipTo, validFrom, validTo", unique = false)))
// Preliminary optimization. Query result cache currently only seems to work with static
// parameterized named queries (eg. not with query dsl, as hints are added dynamically)
// as documented here. http://www.eclipse.org/eclipselink/documentation/2.6/concepts/cache008.htm
@NamedQueries(NamedQuery(name = "Route.find", query = "SELECT r FROM Route r WHERE r.layer = :layer AND r.country = :country " +
        "AND r.zipFrom <= :zipFrom AND r.zipTo >= :zipTo AND r.validFrom < :time " +
        "AND r.validTo > :time", hints = arrayOf(QueryHint(name = QueryHints.QUERY_RESULTS_CACHE, value = "true"), QueryHint(name = QueryHints.QUERY_RESULTS_CACHE_SIZE, value = "500"))))
@Serializable(uid = 0xef6d8232d3263dL)
class Route {
    @Id
    var id: Long? = null
    @Basic
    var layer: Int? = null
    @Basic
    var country: String? = null
    @Basic
    var zipFrom: String? = null
    @Basic
    var zipTo: String? = null
    @Basic
    var validCRTR: Int? = null
    @Basic
    var validFrom: Timestamp? = null
    @Basic
    var validTo: Timestamp? = null
    @Basic
    @Column(nullable = false)
    @Index
    var timestamp: Timestamp? = null
    @Basic
    var station: Int? = null
    @Basic
    var area: String? = null
    @Basic
    var etod: Time? = null
    @Basic
    var ltop: Time? = null
    @Basic
    var term: Int? = null
    @Basic
    var saturdayOK: Int? = null
    @Basic
    var ltodsa: Time? = null
    @Basic
    var ltodholiday: Time? = null
    @Basic
    var island: Int? = null
    @Basic
    var holidayCtrl: String? = null
    @Basic
    @Column(nullable = false)
    var syncId: Long? = null
}
