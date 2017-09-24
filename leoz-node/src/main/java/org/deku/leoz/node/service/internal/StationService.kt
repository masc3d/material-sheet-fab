package org.deku.leoz.node.service.internal

import org.deku.leoz.node.data.jpa.MstStation
import org.deku.leoz.node.data.repository.master.StationRepository
import org.deku.leoz.service.internal.entity.Station
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Created by masc on 17.09.14.
 */
@Named
@Path("internal/v1/station")
class StationService : org.deku.leoz.service.internal.StationService {
    private val log = org.slf4j.LoggerFactory.getLogger(org.deku.leoz.node.service.internal.StationService::class.java)

    @Inject
    private lateinit var stationRepository: StationRepository

    override fun get(): Array<Station> {
        val stations = stationRepository.findAll()
        return stations.map { s -> org.deku.leoz.node.service.internal.StationService.Companion.convert(s) }.toTypedArray()
    }

    override fun find(query: String): Array<Station> {
        val stations = stationRepository.findWithQuery(query)
        return stations.map { s -> org.deku.leoz.node.service.internal.StationService.Companion.convert(s) }.toTypedArray()
    }

    companion object {
        /**
         * Convert to service result
         * @param d
         * *
         * @return
         */
        internal fun convert(d: MstStation): Station {
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
