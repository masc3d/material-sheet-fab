package org.deku.leoz.node.rest.services.internal.v1

import org.deku.leoz.node.data.repositories.master.StationRepository
import org.deku.leoz.rest.entities.internal.v1.Station
import org.slf4j.LoggerFactory
import sx.rs.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Created by masc on 17.09.14.
 */
@Named
@ApiKey(false)
@Path("internal/v1/depot")
class StationService : org.deku.leoz.rest.services.internal.v1.StationService {
    private val log = LoggerFactory.getLogger(StationService::class.java)

    @Inject
    private lateinit var stationRepository: StationRepository

    override fun get(): Array<Station> {
        val stations = stationRepository.findAll()
        return stations.map { s -> convert(s) }.toTypedArray()
    }

    override fun find(query: String): Array<Station> {
        val stations = stationRepository.findWithQuery(query)
        return stations.map { s -> convert(s) }.toTypedArray()
    }

    companion object {
        /**
         * Convert to service result
         * @param d
         * *
         * @return
         */
        internal fun convert(d: org.deku.leoz.node.data.jpa.MstStation): Station {
            val rStation = Station()
            rStation.depotNr = d.stationNr
            rStation.depotMatchcode = d.ustid
            rStation.address1 = d.address1
            rStation.address2= d.address2
            rStation.lkz = d.country
            rStation.ort = d.city
            rStation.plz = d.zip
            rStation.strasse = d.street
            return rStation
        }
    }
}
