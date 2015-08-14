package org.deku.leoz.node.rest.services.v1

import com.google.common.base.Strings
import com.google.common.collect.Iterables
import com.google.common.primitives.Ints
import com.mysema.query.jpa.impl.JPAQuery
import com.mysema.query.types.expr.BooleanExpression
import org.deku.leoz.node.data.entities.master.*
import org.deku.leoz.node.data.repositories.master.*
import org.deku.leoz.node.rest.ServiceException
import org.deku.leoz.rest.entities.ShortDate
import org.deku.leoz.rest.entities.ShortTime
import org.deku.leoz.rest.entities.internal.v1.TestEntry
import org.deku.leoz.rest.entities.v1.DayType
import org.deku.leoz.rest.entities.v1.Routing
import org.deku.leoz.rest.entities.v1.RoutingRequest
import org.deku.leoz.rest.services.ServiceErrorCode
import org.deku.leoz.rest.services.v1
import org.deku.leoz.rest.services.v1.RoutingService
import sx.rs.ApiKey
import java.sql.Timestamp
import java.time.DayOfWeek
import java.time.LocalDate

import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by masc on 23.07.14.
 */
Named
ApiKey(false)
Path("v1/routing")
Produces(MediaType.APPLICATION_JSON)
public class RoutingServiceKt : org.deku.leoz.rest.services.v1.RoutingService {
    @Inject
    var countryRepository: CountryRepository? = null
    @Inject
    var routeRepository: RouteRepository? = null
    @Inject
    var holidayctrlRepostitory: HolidayctrlRepository? = null
    @Inject
    var routingLayerRepository: RoutingLayerRepository? = null
    @Inject
    var stationRepository: StationRepository? = null

    /**
     * Request routing
     * @param routingRequest Routing request
     */
    override fun request(routingRequest: RoutingRequest): Routing {
        val rWSRouting = Routing()

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

        val layer = routingLayerRepository!!.findAll(
                QRoutingLayer.routingLayer.services.eq(ctrlTransportUnit))

        //TODO rWereLayer bitmaske suchen

        val sendDate = routingRequest.sendDate?.getLocalDate()
        val routingValidDate = sendDate
        var desiredDeliveryDate: LocalDate? = routingRequest.desiredDeliveryDate?.getLocalDate()

        var deliveryDate: LocalDate? = null

        var senderParticipant: Routing.Participant? = null
        val possibleSenderSectors = ArrayList<String>()
        if (routingRequest.sender != null) {
            var senderParticipants = queryRoute("S",
                    routingValidDate,
                    sendDate,
                    desiredDeliveryDate,
                    routingRequest.sender,
                    layer,
                    ctrlTransportUnit,
                    "Sender: ")

            for (s in senderParticipants) {
                if (!possibleSenderSectors.contains(s))
                    possibleSenderSectors.add(s.sector)
            }

            senderParticipant = senderParticipants.first()
            if (Strings.isNullOrEmpty(senderParticipant.message))
                rWSRouting.sender = senderParticipant

            senderParticipant.date = null
            senderParticipant.message = null
        } else
            rWSRouting.sender = null


        var consigneeParticipant: Routing.Participant? = null;
        if (routingRequest.consignee != null) {
            var consigneeParticipants = queryRoute("D",
                    routingValidDate,
                    sendDate,
                    desiredDeliveryDate,
                    routingRequest.consignee,
                    layer,
                    ctrlTransportUnit,
                    "Consignee: ")

            for (c in consigneeParticipants) {
                if (!possibleSenderSectors.contains(c))
                    possibleSenderSectors.add(c.sector)
            }

            consigneeParticipant = consigneeParticipants.first()
            if (Strings.isNullOrEmpty(consigneeParticipant.message)) {
                rWSRouting.consignee = consigneeParticipant
                deliveryDate = consigneeParticipant.date
            }
            consigneeParticipant.date = null
            consigneeParticipant.message = null
        } else
            rWSRouting.consignee = null


        val viaHubs = arrayOf("") // {"NST", "N1"};

        rWSRouting.sendDate = ShortDate(sendDate)
        if (deliveryDate != null)
            rWSRouting.deliveryDate = ShortDate(deliveryDate)

        if (consigneeParticipant != null)
            rWSRouting.labelContent = consigneeParticipant.stationFormatted ?: ""

        rWSRouting.viaHubs = viaHubs
        rWSRouting.message = "OK"

        return rWSRouting
    }

    /**
     * Query route
     */
    private fun queryRoute(sendDelivery: String,
                           validDate: LocalDate?,
                           sendDate: LocalDate?,
                           desiredDeliveryDate: LocalDate?,
                           requestParticipant: RoutingRequest.RequestParticipant?,
                           routingLayers: Iterable<RoutingLayer>,
                           ctrl: Int,
                           errorPrefix: String)
            : List<Routing.Participant> {

        val resultParticipants = ArrayList<Routing.Participant>()

        var country: String = requestParticipant?.country?.toUpperCase()
                ?: throw ServiceException(ServiceErrorCode.MISSING_PARAMETER, "${errorPrefix} empty country")

        var zip: String = requestParticipant?.zip?.toUpperCase()
                ?: throw ServiceException(ServiceErrorCode.MISSING_PARAMETER, "${errorPrefix} empty zipcode")

        val rcountry = countryRepository!!.findOne(country)
                ?: throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} unknown country")

        if (Strings.isNullOrEmpty(rcountry.getZipFormat()))
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} unknown country")

        if (zip.length() < rcountry.getMinLen())
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} zipcode too short")

        if (rcountry.getRoutingTyp() < 0 || rcountry.getRoutingTyp() > 3)
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} country not enabled")

        if (zip.length() > rcountry.getMaxLen())
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} zipcode too long")

        val parsedZip = ParsedZip.parseZip(rcountry.getZipFormat(), zip)

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
    private fun queryRouteLayer(sendDelivery: String,
                                requestParticipant: RoutingRequest.RequestParticipant?,
                                queryZipCode: String,
                                validDate: LocalDate?,
                                sendDate: LocalDate?,
                                desiredDeliveryDate: LocalDate?,
                                routingLayer: RoutingLayer,
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
        //                        .and(QRoute.route.validFrom.before(Timestamp.valueOf(validDate.toString() + " 00:00:00")))
        //                        .and(QRoute.route.validTo.after(Timestamp.valueOf(validDate.toString() + " 00:00:00")))
        //        )

        val rRoutes = routeRepository!!.findAll(QRoute.route.layer
                .eq(routingLayer.getLayer())
                .and(QRoute.route.country.eq(requestParticipant?.country?.toUpperCase()))
                .and(QRoute.route.zipFrom.loe(queryZipCode))
                .and(QRoute.route.zipTo.goe(queryZipCode))
                .and(QRoute.route.validFrom.before(Timestamp.valueOf(validDate.toString() + " 00:00:00")))
                .and(QRoute.route.validTo.after(Timestamp.valueOf(validDate.toString() + " 00:00:00"))))

        if (Iterables.isEmpty(rRoutes))
            throw ServiceException(RoutingService.ErrorCode.ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER, "${errorPrefix} no Route found")

        val rRoute = rRoutes.first()

        //TODO Sector aus stationsector

        val rStation = stationRepository!!.findOne(rRoute.getStation())

        if (rStation == null)
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, "${errorPrefix} Route Station not found");

        participant.sector = rStation.getSector()
        participant.country = rRoute.getCountry()
        participant.zipCode = queryZipCode
        participant.term = rRoute.getTerm()

        when(sendDelivery) {
            "S" -> {
                //                mqueryRouteLayer.setDayType(getDayType(sendDate, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl()).toString());
                //TODO nächsten Linientag ermitteln
                participant.date = sendDate
                //                mqueryRouteLayer.setDate(getNextDeliveryDay(sendDate, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl()));
                //                if (date.equals(null))
                //                    date = getNextDeliveryDay(date, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl());
            }
            "D" -> {
                var deliveryDate = desiredDeliveryDate
                        ?: getNextDeliveryDay(sendDate, participant.country, rRoute.getHolidayCtrl())

                participant.date = deliveryDate
            }
        }

        participant.dayType =
                this.getDayType(
                        participant.date,
                        requestParticipant?.country?.toUpperCase(),
                        rRoute.getHolidayCtrl()
                ).toString()

        participant.station = rRoute.getStation()
        participant.zone = rRoute.getArea()
        participant.island = (rRoute.getIsland() != 0)
        participant.earliestTimeOfDelivery = sqlTimeToShortTime(rRoute.getEtod())
        participant.earliestTimeOfDelivery = ShortTime(rRoute.getEtod().toString())
        participant.term = rRoute.getTerm()
        if (rRoute.getLtodsa() != null)
            participant.sundayDeliveryUntil = ShortTime(rRoute.getLtodsa().toString())

        return participant
    }


    /**
     * Get day type
     */
    private fun getDayType(date: LocalDate?, country: String?, holidayCtrl: String): DayType {
        var daytype = when (date?.getDayOfWeek()) {
            DayOfWeek.SUNDAY -> DayType.Sunday
            DayOfWeek.SATURDAY -> DayType.Saturday
            else -> DayType.Workday
        }

        val rHolidayCtrl = holidayctrlRepostitory!!.findOne(
                HolidayCtrlPK(java.sql.Timestamp.valueOf(date.toString() + " 00:00:00"), country))

        if (rHolidayCtrl != null) {
            if (rHolidayCtrl.getCtrlPos() == -1)
                daytype = DayType.Holiday
            else if (rHolidayCtrl.getCtrlPos() > 0) {
                if (holidayCtrl.charAt(rHolidayCtrl.getCtrlPos()) == 'J')
                    daytype = DayType.RegionalHoliday
            }
        }

        return daytype
    }

    /**
     * Get next delivery day
     */
    private fun getNextDeliveryDay(date: LocalDate?, country: String, holidayCtrl: String): LocalDate? {
        var date = date

        var workDaysRequired: Int = 1
        if (this.getDayType(date, country, holidayCtrl) != DayType.Workday)
            workDaysRequired = 2

        var workDays: Int = 0
        do {
            date = date?.plusDays(1)
            if (getDayType(date, country, holidayCtrl) == DayType.Workday)
                workDays++
        } while (workDays < workDaysRequired)

        return date
    }

    companion object {
        public fun sqlTimeToShortTime(time: java.sql.Time?): ShortTime? {
            return if (time != null) ShortTime(time.toString()) else null
        }
    }
}

/**
 * Parsed zip
 */
class ParsedZip(var query: String, var conform: String) {
    companion object {
        /**
         * Parse zip
         */
        public fun parseZip(zipFormat: String, zip: String): ParsedZip {
            var zipQuery = ""
            var zipConform = ""
            val cZipFormat = zipFormat
            val cZip = zip

            var i = 0
            var k = 0
            var csZipFormat = ""
            var csZip = ""
            var csNew = ""

            var cCount = 0

            var validZip = true

            while (k < zipFormat.length() && validZip) {
                if (i + 1 > cZip.length())
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
                        i = i + 1
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
                    else if (csZip.contains("abcdefghijklmnopqrstuvwxyz0123456789 ")) {
                        i++
                        k++
                        csNew = csZip
                    } else {
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

            return ParsedZip(zipQuery, zipConform)
        }
    }
}