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
    var mCountryRepository: CountryRepository? = null
    @Inject
    var mRouteRepository: RouteRepository? = null
    @Inject
    var mHolidayctrlRepostitory: HolidayctrlRepository? = null
    @Inject
    var mRoutingLayerRepository: RoutingLayerRepository? = null
    @Inject
    var mStationRepository: StationRepository? = null

    /**
     * Request routing
     * @param routingRequest Routing request
     */
    override fun request(routingRequest: RoutingRequest): Routing {
        val rWSRouting = Routing()

        if (routingRequest.getSendDate() == null) {
            throw ServiceException(ServiceErrorCode.MISSING_PARAMETER, "Send Date is required")
        }

        if (routingRequest.getSender() == null && routingRequest.getConsignee() == null) {
            throw ServiceException(ServiceErrorCode.MISSING_PARAMETER, "Sender or Consignee required")
        }

        val services: Int = routingRequest.getServices() ?: 0
        // TODO REAL oder Volumen ???
        val weight: Double = routingRequest.getWeight()?.toDouble() ?: 0.0

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

        val layer = mRoutingLayerRepository!!.findAll(
                QRoutingLayer.routingLayer.services.eq(ctrlTransportUnit))

        //TODO rWereLayer bitmaske suchen

        val sendDate = routingRequest.getSendDate().getLocalDate()
        val routingValidDate = sendDate
        var desiredDeliveryDate: LocalDate? = routingRequest.getDesiredDeliveryDate()?.getLocalDate()

        var deliveryDate: LocalDate? = null

        val possibleSenderSectors = ArrayList<String>()
        if (routingRequest.getSender() != null) {
            var routingParticipantSender = queryRoute("S",
                    routingValidDate,
                    sendDate,
                    desiredDeliveryDate,
                    routingRequest.getSender(),
                    layer,
                    ctrlTransportUnit,
                    "Sender: ")

            val s = routingParticipantSender.iterator()
            while (s.hasNext()) {
                if (!possibleSenderSectors.contains(s)) {
                    possibleSenderSectors.add(s.next().getSector())
                }
            }
            if (routingParticipantSender.iterator().next().getMessage() == "")
                rWSRouting.setSender(routingParticipantSender.iterator().next())
            routingParticipantSender.iterator().next().setDate(null)
            routingParticipantSender.iterator().next().setMessage(null)
        } else
            rWSRouting.setSender(null)


        var routingParticipantConsignee: Iterable<Routing.Participant>? = null
        if (routingRequest.getConsignee() != null) {
            routingParticipantConsignee = queryRoute("D",
                    routingValidDate,
                    sendDate,
                    desiredDeliveryDate,
                    routingRequest.getConsignee(),
                    layer,
                    ctrlTransportUnit,
                    "Consignee: ")

            val c = routingParticipantConsignee.iterator()
            while (c.hasNext()) {
                if (!possibleSenderSectors.contains(c))
                    possibleSenderSectors.add(c.next().getSector())
            }
            if (routingParticipantConsignee.iterator().next().getMessage() == "") {
                rWSRouting.setConsignee(routingParticipantConsignee.iterator().next())
                deliveryDate = routingParticipantConsignee.iterator().next().getDate()
            }
            routingParticipantConsignee.iterator().next().setDate(null)
            routingParticipantConsignee.iterator().next().setMessage(null)
        } else
            rWSRouting.setConsignee(null)


        val viaHubs = arrayOf("")// {"NST", "N1"};

        rWSRouting.setSendDate(ShortDate(sendDate))
        if (deliveryDate != null)
            rWSRouting.setDesiredDeliveryDate(ShortDate(deliveryDate))

        var labelContent = ""
        if (routingRequest.getConsignee() != null)
            labelContent += com.google.common.base.Strings.padEnd(routingParticipantConsignee!!.iterator().next().getStation().toString(), 3, '0')
        rWSRouting.setLabelContent(labelContent)

        rWSRouting.setViaHubs(viaHubs)
        rWSRouting.setMessage("OK")

        return rWSRouting
    }

    /**
     * Query route
     */
    private fun queryRoute(sendDelivery: String,
                           validDate: LocalDate,
                           sendDate: LocalDate,
                           desiredDeliveryDate: LocalDate?,
                           requestParticipant: RoutingRequest.RequestParticipant,
                           routingLayers: Iterable<RoutingLayer>,
                           ctrl: Int,
                           exeptionPrefix: String)
            : List<Routing.Participant> {

        val resultParticipants = ArrayList<Routing.Participant>()

        var country: String = requestParticipant.getCountry()?.toUpperCase()
                ?: throw ServiceException(ServiceErrorCode.MISSING_PARAMETER, exeptionPrefix + "empty country")

        var zip: String = requestParticipant.getZip()?.toUpperCase()
                ?: throw ServiceException(ServiceErrorCode.MISSING_PARAMETER, exeptionPrefix + "empty zipcode")

        val rcountry = mCountryRepository!!.findOne(country)
                ?: throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, exeptionPrefix + "unknown country")

        if (Strings.isNullOrEmpty(rcountry.getZipFormat()))
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, exeptionPrefix + "unknown country")

        if (zip.length() < rcountry.getMinLen())
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, exeptionPrefix + "zipcode too short")

        if (rcountry.getRoutingTyp() < 0 || rcountry.getRoutingTyp() > 3)
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, exeptionPrefix + "country not enabled")

        if (zip.length() > rcountry.getMaxLen())
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, exeptionPrefix + "zipcode too long")

        val parsedZip = ParsedZip.parseZip(rcountry.getZipFormat(), zip)

        if (Strings.isNullOrEmpty(parsedZip.query))
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, exeptionPrefix + "wrong zipcode format")

        //TODO verbessern ?
        for (routingLayer in routingLayers) {
            val resultParticipantLayer = queryRouteLayer(sendDelivery,
                    requestParticipant,
                    parsedZip.query,
                    validDate,
                    sendDate,
                    desiredDeliveryDate,
                    routingLayer,
                    ctrl,
                    exeptionPrefix)

            if (resultParticipantLayer.getStation() != "0") {
                resultParticipantLayer.setZipCode(parsedZip.conform)
                resultParticipants.add(resultParticipantLayer)
            }
        }

        return resultParticipants
    }

    /**
     * Query route layer
     */
    private fun queryRouteLayer(sendDelivery: String,
                                requestParticipant: RoutingRequest.RequestParticipant,
                                queryZipCode: String,
                                validDate: LocalDate,
                                sendDate: LocalDate,
                                desiredDeliveryDate: LocalDate?,
                                routingLayer: RoutingLayer,
                                ctrl: Int,
                                exeptionPrefix: String)
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

        val rRoutes = mRouteRepository!!.findAll(QRoute.route.layer
                .eq(routingLayer.getLayer())
                .and(QRoute.route.country.eq(requestParticipant.getCountry().toUpperCase()))
                .and(QRoute.route.zipFrom.loe(queryZipCode))
                .and(QRoute.route.zipTo.goe(queryZipCode))
                .and(QRoute.route.validFrom.before(Timestamp.valueOf(validDate.toString() + " 00:00:00")))
                .and(QRoute.route.validTo.after(Timestamp.valueOf(validDate.toString() + " 00:00:00"))))

        if (Iterables.isEmpty(rRoutes))
            throw ServiceException(RoutingService.ErrorCode.ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER, exeptionPrefix + "no Route found")

        val rRoute = rRoutes.first()

        //TODO Sector aus stationsector

        val rStation = mStationRepository!!.findOne(rRoute.getStation())

        if (rStation == null)
            throw ServiceException(ServiceErrorCode.WRONG_PARAMETER_VALUE, exeptionPrefix + "Route Station not found");

        participant.setSector(rStation.getSector())
        participant.setCountry(rRoute.getCountry())
        participant.setZipCode(queryZipCode)
        participant.setTerm(rRoute.getTerm())

        when(sendDelivery) {
            "S" -> {
                //                mqueryRouteLayer.setDayType(getDayType(sendDate, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl()).toString());
                //TODO nächsten Linientag ermitteln
                participant.setDate(sendDate)
                //                mqueryRouteLayer.setDate(getNextDeliveryDay(sendDate, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl()));
                //                if (date.equals(null))
                //                    date = getNextDeliveryDay(date, mqueryRouteLayer.getCountry(), routeFound.getHolidayCtrl());
            }
            "D" -> {
                var deliveryDate = desiredDeliveryDate
                        ?: getNextDeliveryDay(sendDate, participant.getCountry(), rRoute.getHolidayCtrl())

                participant.setDate(deliveryDate)
            }
        }

        participant.setDayType(
                this.getDayType(
                        participant.getDate(),
                        requestParticipant.getCountry().toUpperCase(),
                        rRoute.getHolidayCtrl()
                ).toString())

        participant.setStation(rRoute.getStation())
        participant.setZone(rRoute.getArea())
        participant.setIsland(rRoute.getIsland() !== 0)
        participant.setEarliestTimeOfDelivery(sqlTimeToShortTime(rRoute.getEtod()))
        participant.setEarliestTimeOfDelivery(ShortTime(rRoute.getEtod().toString()))
        participant.setTerm(rRoute.getTerm())
        if (rRoute.getLtodsa() != null)
            participant.setSundayDeliveryUntil(ShortTime(rRoute.getLtodsa().toString()))

        return participant
    }


    /**
     * Get day type
     */
    private fun getDayType(date: LocalDate, country: String, holidayCtrl: String): DayType {
        var daytype = when (date.getDayOfWeek()) {
            DayOfWeek.SUNDAY -> DayType.Sunday
            DayOfWeek.SATURDAY -> DayType.Saturday
            else -> DayType.Workday
        }

        val rHolidayCtrl = mHolidayctrlRepostitory!!.findOne(
                HolidayCtrlPK(java.sql.Timestamp.valueOf(date.toString() + " 00:00:00"), country))

        if (rHolidayCtrl != null) {
            if (rHolidayCtrl.getCtrlPos() === -1)
                daytype = DayType.Holiday
            else if (rHolidayCtrl.getCtrlPos() > 0) {
                if (holidayCtrl.substring(rHolidayCtrl.getCtrlPos()!!, rHolidayCtrl.getCtrlPos()!!) === "J")
                    daytype = DayType.RegionalHoliday
            }
        }

        return daytype
    }

    /**
     * Get next delivery day
     */
    private fun getNextDeliveryDay(date: LocalDate, Country: String, Holidayctrl: String): LocalDate {
        var date = date
        var forDeliveryNecessaryWorkdays: Int = 1
        if (getDayType(date, Country, Holidayctrl) != DayType.Workday)
            forDeliveryNecessaryWorkdays = 2

        var wokdays: Int = 0
        do {
            date = date.plusDays(1)
            if (getDayType(date, Country, Holidayctrl) == DayType.Workday)
                wokdays++
        } while (wokdays < forDeliveryNecessaryWorkdays)

        return date
    }

    companion object {
        public fun sqlTimeToShortTime(time: java.sql.Time?): ShortTime? {
            if (time == null)
                return null
            return ShortTime(time.toString())
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