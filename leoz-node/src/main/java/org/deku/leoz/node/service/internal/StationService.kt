package org.deku.leoz.node.service.internal

import org.deku.leoz.node.data.jpa.MstStation
import org.deku.leoz.node.data.repository.master.StationRepository
import sx.rs.DefaultProblem
import org.deku.leoz.service.internal.entity.Station
import org.deku.leoz.service.internal.entity.StationV2
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response

//import org.deku.leoz.central.data.repository.UserJooqRepository

/**
 * Created by masc on 17.09.14.
 */
@Named
@Path("internal/v1/station")
class StationService : org.deku.leoz.service.internal.StationService {
    private val log = org.slf4j.LoggerFactory.getLogger(org.deku.leoz.node.service.internal.StationService::class.java)

    @Context
    private lateinit var httpHeaders: HttpHeaders

    @Inject
    private lateinit var stationRepository: StationRepository

    //@Inject
    //private lateinit var userRepository: UserJooqRepository

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
            rStation.address2 = d.address2
            rStation.lkz = d.country
            rStation.ort = d.city
            rStation.plz = d.zip
            rStation.strasse = d.street
            return rStation
        }
    }

    override fun getByStationNo(stationNo: Int): StationV2 {

        //
        //val apiKey = this.httpHeaders.getHeaderString(Rest.API_KEY)
        //apiKey ?:
//                throw DefaultProblem(status = Response.Status.UNAUTHORIZED)

        //      val authorizedUserRecord = userRepository.findByKey(apiKey)
        //authorizedUserRecord ?:
        //      throw DefaultProblem(status = Response.Status.UNAUTHORIZED)
        val station = stationRepository.findByStation(stationNo)
        station ?: throw DefaultProblem(status = Response.Status.BAD_REQUEST, title = "station not found")
        return station.toStationV2()
    }


    override fun getByDebitorId(debitorId: Int): Array<StationV2> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun MstStation.toStationV2(): StationV2 {
    val stationV2 = StationV2(
            stationNo = this.stationNr,
            stationMatchcode = null,
            address1 = this.address1,
            address2 = this.address2,
            country = this.country,
            zip = this.zip,
            city = this.city,
            street = this.street,
            houseNo = this.houseNr,
            sector = this.sector,
            valuablesAllowed = false,
            valuablesWithoutBagAllowed = false
    )
    return stationV2
}
