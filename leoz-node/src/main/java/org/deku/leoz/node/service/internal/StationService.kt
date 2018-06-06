package org.deku.leoz.node.service.internal

import org.deku.leoz.node.data.jpa.MstStation
import org.deku.leoz.node.data.jpa.QMstStation
import org.deku.leoz.node.data.repository.StationContractRepository
import org.deku.leoz.node.data.repository.StationRepository
import org.deku.leoz.service.internal.entity.Address
import org.deku.leoz.service.internal.entity.GeoLocation
import org.deku.leoz.service.internal.entity.Station
import org.deku.leoz.service.internal.entity.StationV2
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sx.rs.RestProblem
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response

/**
 * Created by masc on 17.09.14.
 */
@Component
@Path("internal/v1/station")
class StationService : org.deku.leoz.service.internal.StationService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Context
    private lateinit var httpHeaders: HttpHeaders

    @Inject
    private lateinit var stationRepository: StationRepository

    @Inject
    private lateinit var stationContractRepo: StationContractRepository

    @Context
    private lateinit var httpRequest: HttpServletRequest

    override fun get(): Array<Station> {
        // TODO: verify / filter result by authorized user debitor

        val stations = stationRepository.findAll()
        return stations
                .map { it.toStation() }
                .toTypedArray()
    }

    override fun find(query: String): Array<Station> {
        // TODO: verify / filter result by authorized user debitor

        val stations = stationRepository.findWithQuery(query)
        return stations
                .map { it.toStation() }
                .toTypedArray()
    }

    /** */
    override fun getByStationNo(stationNo: Int): StationV2 {
        // TODO: verify / filter result by authorized user debitor
        val station = stationRepository
                .findByStationNo(stationNo)
                ?: throw RestProblem(status = Response.Status.NOT_FOUND, title = "Station not found")


        return station.toStationV2()
    }

    /** */
    override fun getByDebitorId(debitorId: Int): Array<StationV2> {
        val stationIds = stationContractRepo
                .findStationIds(debitorId)
                .also {
                    if (it.count() == 0)
                        throw RestProblem(status = Response.Status.NOT_FOUND, title = "Station IDs not found")
                }

        val stations = stationRepository
                .findAll(QMstStation.mstStation
                        .stationId.`in`(*stationIds.toTypedArray()))
                .also {
                    if (it.count() == 0)
                        throw RestProblem(status = Response.Status.NOT_FOUND, title = "Stations not found")
                }

        return stations.map { s -> s.toStationV2() }.toTypedArray()
    }

    //region Transformations
    /**
     * Convert to service result
     */
    fun MstStation.toStation(): Station {
        val d = this
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

    /**
     * Convert to v2 service result
     */
    fun MstStation.toStationV2(): StationV2 {
        val stationV2 = StationV2(
                stationNo = this.stationNr,
                stationMatchcode = null,
                address = Address(
                        line1 = this.address1,
                        line2 = this.address2,
                        line3 = null,
                        phoneNumber = this.phone1,
                        countryCode = this.country,
                        zipCode = this.zip,
                        city = this.city,
                        street = this.street,
                        streetNo = this.houseNr,
                        geoLocation = GeoLocation(latitude = this.posLat, longitude = this.posLong)),
                sector = this.sector,
                exportValuablesAllowed = ((this.exportValuablesAllowed ?: 0) != 0),
                exportValuablesWithoutBagAllowed = ((this.exportValuablesWithoutBagAllowed ?: 0) != 0)
        )
        return stationV2
    }
    //endregion
}



