package org.deku.leoz.node.service.pub

import com.google.common.base.Strings
import com.google.common.collect.Iterables
import com.google.common.primitives.Ints
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.data.jpa.MstRoutingLayer
import org.deku.leoz.node.data.jpa.QMstHolidayCtrl
import org.deku.leoz.node.data.jpa.QMstRoutingLayer
import org.deku.leoz.node.data.repository.*
import org.deku.leoz.node.rest.ServiceException
import org.deku.leoz.service.entity.DayType
import org.deku.leoz.service.entity.ServiceErrorCode
import org.deku.leoz.service.pub.RoutingService.*
import org.deku.leoz.time.ShortDate
import org.deku.leoz.time.ShortTime
import org.deku.leoz.time.toShortTime
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sx.rs.RestProblem
import sx.time.toDate
import sx.time.toLocalDate
import sx.time.toTimestamp
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit
import javax.ws.rs.Path

/**
 * Created by masc on 23.07.14.
 */
@Component
@Path("v1/routing")
class RoutingService : org.deku.leoz.service.pub.RoutingService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    lateinit var countryRepository: CountryRepository
    @Inject
    lateinit var routeRepository: RouteRepository
    @Inject
    lateinit var holidayCtrlJpaRepostitory: HolidayCtrlRepository
    @Inject
    lateinit var routingLayerRepository: RoutingLayerRepository
    @Inject
    lateinit var stationRepository: StationRepository

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    lateinit var entityManagerFactory: EntityManagerFactory

    /**
     * Request routing
     * @param routingRequest Routing request
     */
    override fun request(routingRequest: Request): Routing {
//        log.info(">>> ${Thread.currentThread().id} START")
        val routing = Routing()

        if (routingRequest.sendDate == null)
            throw ServiceException(ServiceErrorCode.MISSING_PARAMETER, "Send date is required")

        if (routingRequest.sender == null && routingRequest.consignee == null)
            throw ServiceException(ServiceErrorCode.MISSING_PARAMETER, "Sender or consignee required")

        val services: Int = routingRequest.services ?: 0

        // TODO REAL oder Volumen ???
        val weight: Double = routingRequest.weight?.toDouble() ?: 0.0

        // Unit CTRLs
        var ctrlTransportUnit: Int = 1

        if (weight > 100)
            ctrlTransportUnit += 2

        if ((services and 256) != 0)
            ctrlTransportUnit += 4

        if ((services and 512) != 0)
            ctrlTransportUnit += 8

        // check usable Layers
        //TODO
        ctrlTransportUnit = 23

        val layer = routingLayerRepository.findAll(
                QMstRoutingLayer.mstRoutingLayer.services.eq(ctrlTransportUnit))

        //TODO rWereLayer bitmaske suchen

        val sendDate = routingRequest.sendDate?.date?.toLocalDate()
        val routingValidDate = sendDate
        val desiredDeliveryDate: LocalDate? = routingRequest.desiredDeliveryDate?.date?.toLocalDate()

        var deliveryDate: LocalDate? = null

        val senderParticipant: Routing.Participant?
        val possibleSenderSectors = ArrayList<String>()
        if (routingRequest.sender != null) {
            val senderParticipants = queryRoute("S",
                    routingValidDate,
                    sendDate,
                    desiredDeliveryDate,
                    routingRequest.sender,
                    layer,
                    ctrlTransportUnit,
                    "Sender: ")

            for (s in senderParticipants) {
                if (!possibleSenderSectors.contains(s.sector))
                    possibleSenderSectors.add(s.sector)
            }

            senderParticipant = senderParticipants.first()
            if (Strings.isNullOrEmpty(senderParticipant.message))
                routing.sender = senderParticipant

            senderParticipant.date = null
            senderParticipant.message = null
        } else
            routing.sender = null


        var consigneeParticipant: Routing.Participant? = null
        if (routingRequest.consignee != null) {
            val consigneeParticipants = queryRoute("D",
                    routingValidDate,
                    sendDate,
                    desiredDeliveryDate,
                    routingRequest.consignee,
                    layer,
                    ctrlTransportUnit,
                    "Consignee: ")

            for (c in consigneeParticipants) {
                if (!possibleSenderSectors.contains(c.sector))
                    possibleSenderSectors.add(c.sector)
            }

            consigneeParticipant = consigneeParticipants.first()
            if (Strings.isNullOrEmpty(consigneeParticipant.message)) {
                routing.consignee = consigneeParticipant
                deliveryDate = consigneeParticipant.date?.toLocalDate()
            }
            consigneeParticipant.date = null
            consigneeParticipant.message = null
        } else
            routing.consignee = null


        val viaHubs = arrayOf("") // {"NST", "N1"};

        routing.sendDate = ShortDate(sendDate!!.toDate())
        routing.deliveryDate = if (deliveryDate != null) ShortDate(deliveryDate.toDate()) else null
        routing.labelContent = if (consigneeParticipant != null) consigneeParticipant.stationFormatted ?: "" else ""
        routing.viaHubs = viaHubs
        routing.message = "OK"

//        log.info("<<< ${Thread.currentThread().id} END")

        return routing
    }

    /**
     * Query route
     */
    private fun queryRoute(sendDelivery: String,
                           validDate: LocalDate?,
                           sendDate: LocalDate?,
                           desiredDeliveryDate: LocalDate?,
                           requestParticipant: Request.Participant?,
                           routingLayers: Iterable<MstRoutingLayer>,
                           ctrl: Int,
                           errorPrefix: String)
            : List<Routing.Participant> {

        val resultParticipants = ArrayList<Routing.Participant>()

        val country: String = requestParticipant?.country?.toUpperCase()
                ?: throw ServiceException(ServiceErrorCode.MISSING_PARAMETER, "${errorPrefix} empty country")

        val zip: String = requestParticipant.zip?.toUpperCase()
                ?: throw ServiceException(ServiceErrorCode.MISSING_PARAMETER, "${errorPrefix} empty zipcode")

        val rcountry = countryRepository.findById(country).orElse(null)
                ?: throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} unknown country")

        if (Strings.isNullOrEmpty(rcountry.zipFormat))
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} unknown country")

        if (rcountry.minLen == null ||
                rcountry.maxLen == null ||
                rcountry.zipFormat == null)
            throw RestProblem(detail = "${errorPrefix} unsupported country")

        if (zip.length < rcountry.minLen!!)
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} zipcode too short")

        if (rcountry.routingTyp!! < 0 || rcountry.routingTyp!! > 3)
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} country not enabled")

        if (zip.length > rcountry.maxLen!!)
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} zipcode too long")

        val parsedZip = ParsedZip(zip = zip, zipFormat = rcountry.zipFormat!!)

        if (Strings.isNullOrEmpty(parsedZip.query))
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} wrong zipcode format")

        //TODO verbessern ?
        for (routingLayer in routingLayers) {
            val participant = queryRouteLayer(sendDelivery,
                    requestParticipant,
                    parsedZip.query,
                    validDate,
                    sendDate,
                    desiredDeliveryDate,
                    routingLayer,
                    ctrl,
                    errorPrefix)

            if (participant.station != 0) {
                participant.zipCode = parsedZip.conform
                resultParticipants.add(participant)
            }
        }

        return resultParticipants
    }

    /**
     * Query route layer
     */
    @Suppress("UNUSED_PARAMETER")
    private fun queryRouteLayer(sendDelivery: String,
                                requestParticipant: Request.Participant?,
                                queryZipCode: String,
                                validDate: LocalDate?,
                                sendDate: LocalDate?,
                                desiredDeliveryDate: LocalDate?,
                                routingLayer: MstRoutingLayer,
                                ctrl: Int,
                                errorPrefix: String)
            : Routing.Participant {

        val participant = Routing.Participant()

        // TODO Join
        //        val query = JPAQuery()
        //        query.from(QRoute.route)
        //                .innerJoin(QStation.station).where(
        //                QRoute.route.layer
        //                        .eq(routingLayer.getLayer())
        //                        .and(QRoute.route.country.eq(requestParticipant.getCountry().toUpperCase()))
        //                        .and(QRoute.route.zipFrom.loe(queryZipCode))
        //                        .and(QRoute.route.zipTo.goe(queryZipCode))
        //                        .and(QRoute.route.validFrom.before(validDate?.toTimestamp()))
        //                        .and(QRoute.route.validTo.after(validDate?.toTimestamp()))
        //        )

        // TODO: preliminary optimization: named query with query result cache support
//        val em = entityManagerFactory.createEntityManager()
//        val emq = em.createNamedQuery("Route.find", Route::class.java)
//
//        emq.setParameter("layer", routingLayer.layer)
//        emq.setParameter("country", requestParticipant?.country?.toUpperCase())
//        emq.setParameter("zipFrom", queryZipCode)
//        emq.setParameter("zipTo", queryZipCode)
//        emq.setParameter("time", validDate?.toTimestamp())
//
//        val rRoutes = emq.resultList
//
//        val qRoute = QMstRoute.mstRoute

        val rRoutes = routeRepository.findRouteQuery.create { query, p ->
            query
                    .set(p.layer, routingLayer.layer)
                    .set(p.country, requestParticipant?.country?.toUpperCase())
                    .set(p.zip, queryZipCode)
                    .set(p.validDate, validDate?.toTimestamp())
        }
                .execute()
                // Sorting a few records in memory is much faster than adding sort criteria to complex query with h2
                .sortedByDescending {
                    it.syncId
                }

        if (Iterables.isEmpty(rRoutes))
            throw ServiceException(ErrorCode.ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER, "${errorPrefix} no Route found")

        val rRoute = rRoutes.first()

        //TODO Sector aus stationsector

        val rStation = stationRepository.findByStationNo(rRoute.station)

        if (rStation == null)
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} Route Station not found")

        participant.sector = rStation.sector ?: ""
        participant.country = rRoute.country
        participant.zipCode = queryZipCode
        participant.term = rRoute.term ?: 1

        when (sendDelivery) {
            "S" -> {
                //                mqueryRouteLayer.setDayType(getDayType(sendDate, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl()).toString());
                //TODO nächsten Linientag ermitteln
                participant.date = sendDate?.toDate()
                //                mqueryRouteLayer.setDate(getNextDeliveryDay(sendDate, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl()));
                //                if (date.equals(null))
                //                    date = getNextDeliveryDay(date, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl());
            }
            "D" -> {
                val deliveryDate = desiredDeliveryDate
                        ?: getNextDeliveryDay(sendDate, participant.country, rRoute.holidayCtrl!!, participant.term)

                participant.date = deliveryDate.toDate()
            }
        }

        participant.dayType =
                this.getDayType(
                        participant.date?.toLocalDate(),
                        requestParticipant?.country?.toUpperCase(),
                        rRoute.holidayCtrl!!
                ).toString()

        participant.station = rRoute.station!!
        participant.zone = rRoute.area!!
        participant.island = (rRoute.island != 0)
        participant.earliestTimeOfDelivery = rRoute.etod!!.toShortTime()
        participant.term = rRoute.term!!
        if (rRoute.saturdayOk != 0 && rRoute.ltodsa.toShortTime() != ShortTime(localTime = "00:00"))
            participant.saturdayDeliveryUntil = rRoute.ltodsa.toShortTime()
        if (rRoute.ltodholiday.toShortTime() != ShortTime(localTime = "00:00"))
            participant.sundayDeliveryUntil = rRoute.ltodholiday.toShortTime()

        return participant
    }

    /**
     * Get day type
     */
    private fun getDayType(date: LocalDate?, country: String?, holidayCtrl: String): DayType {
        var daytype = when (date?.dayOfWeek) {
            DayOfWeek.SUNDAY -> DayType.Sunday
            DayOfWeek.SATURDAY -> DayType.Saturday
            else -> DayType.Workday
        }

        val qHolidayCtrl = QMstHolidayCtrl.mstHolidayCtrl
        val rHolidayCtrl = holidayCtrlJpaRepostitory.findOne(
                qHolidayCtrl.holiday.eq(date?.toTimestamp())
                        .and(qHolidayCtrl.country.eq(country)))
                .orElse(null)

        if (rHolidayCtrl != null) {
            if (rHolidayCtrl.ctrlPos == -1)
                daytype = DayType.Holiday
            else if (rHolidayCtrl.ctrlPos!! >= 0) {
                if (holidayCtrl[rHolidayCtrl.ctrlPos!!] == 'J')
                    daytype = DayType.RegionalHoliday
            }
        }

        return daytype
    }

    /**
     * Get next delivery day
     */
    private fun getNextDeliveryDay(date: LocalDate?, country: String, holidayCtrl: String, term: Int): LocalDate {
        var day = date

        var workDaysRequired: Int = term
        if (this.getDayType(day, country, holidayCtrl) != DayType.Workday)
            workDaysRequired++

        var workDays: Int = 0
        do {
            day = day?.plusDays(1)
            if (getDayType(day, country, holidayCtrl) == DayType.Workday)
                workDays++
        } while (workDays < workDaysRequired)

        return day!!
    }

    /**
     * Parsed zip
     */
    private class ParsedZip(val zip: String, val zipFormat: String) {
        val query: String
        val conform: String

        init {
            var zipQuery = ""
            var zipConform = ""
            val cZipFormat = this.zipFormat
            val cZip = this.zip

            var i = 0
            var k = 0
            var csZipFormat: String
            var csZip: String
            var csNew: String

            var cCount = 0

            var validZip = true

            while (i < cZip.length && k <= this.zipFormat.length && validZip) {
                if (i + 1 > cZip.length)
                    csZip = ""
                else
                    csZip = cZip.get(i).toString()

                csZipFormat = cZipFormat.get(k).toString()
                csNew = ""
                when (csZipFormat) {
                    "w" -> if (csZip == " ")
                        i++
                    else if (csZip == "0") {
                        i++
                        k++
                    } else
                        k++
                    "0" -> if (csZip == "") {
                        i++
                        k++
                    } else if (csZip == " ") {
                        i++
                    } else if (Ints.tryParse(csZip) == null) {
                        validZip = false
                    } else {
                        i++
                        k++
                        csNew = csZip
                    }
                    "A" -> if (csZip === "") {
                        csNew = ""
                        zipQuery = ""
                    } else if (csZip === " ") {
                        validZip = false
                    } else if (Ints.tryParse(csZip) != null) {
                        validZip = false
                    } else {
                        i++
                        k++
                        csNew = csZip
                    }
                    "L" -> if (csZip == "")
                        validZip = false
                    else if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ".contains(csZip)) {
                        i++
                        k++
                        csNew = csZip
                    } else {
                        validZip = false
                        zipConform = ""
                    }
                    "G" -> {
                        if (csZip == " ")
                            cCount++
                        if (cCount > 1) {
                            zipConform = ""
                        }
                        i++
                        k++
                        csNew = csZip
                    }
                    "-" -> if (csZip == "-") {
                        i++
                        k++
                        csNew = csZip
                    } else if (Ints.tryParse(csZip) == null) {
                        csNew = ""
                    } else {
                        i++
                        k = k + 2
                        csNew = "-" + csZip
                    }
                    else -> throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "wrong zipcode formatdescription")
                }

                zipConform = zipConform + csNew
                if (cCount == 0)
                    zipQuery = zipQuery + csNew

                if (!validZip)
                    zipQuery = ""

            }

            this.query = zipQuery
            this.conform = zipConform
        }
    }
}
