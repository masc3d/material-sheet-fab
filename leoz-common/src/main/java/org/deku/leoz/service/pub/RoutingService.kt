package org.deku.leoz.service.pub

import io.swagger.annotations.*
import org.deku.leoz.service.entity.DayType
import org.deku.leoz.service.entity.ServiceError
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.entity.ShortTime
import sx.rs.auth.ApiKey
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/routing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Routing operations")
@ApiKey(false)
interface RoutingService {
    /**
     * Routing service specific error codes
     */
    enum class ErrorCode(private val value: Int) {
        ROUTE_NOT_AVAILABLE_FOR_GIVEN_PARAMETER(1000)
    }

    /**
     * Created by masc on 23.06.15.
     */
    @ApiModel(value = "RoutingRequest", subTypes = [(Request.Participant::class)], description = "Routing request")
    class Request {
        /**
         * Sender or consignee attributes
         * Created by masc on 23.06.15.
         */
        @ApiModel(value = "RequestParticipant", description = "Request participant. Sender or consignee")
        class Participant {
            @ApiModelProperty(dataType = "string", example = "DE", value = "Country two-letter ISO-3166", position = 10, required = true)
            var country: String? = null

            @ApiModelProperty(dataType = "string", example = "36286", value = "Zip code accordant to country specification", position = 20, required = true)
            var zip: String? = null

            @ApiModelProperty(dataType = "string", example = "09:00", position = 40, required = false, value = "Time window (from)", allowableValues = "00:00 - 23:59")
            var timeFrom: String? = null

            @ApiModelProperty(dataType = "string", example = "12:00", position = 50, required = false, value = "Time window (to)", allowableValues = "00:00 - 23:59")
            var timeTo: String? = null

            @ApiModelProperty(dataType = "string", example = "020", position = 60, required = false, value = "Desired station number", allowableValues = "010 - 999")
            var desiredStation: String? = null

            constructor() {}

            constructor(country: String? = null, zip: String? = null, timeFrom: String? = null, timeTo: String? = null, desiredStation: String? = null) {
                this.country = country
                this.zip = zip
                this.timeFrom = timeFrom
                this.timeTo = timeTo
                this.desiredStation = desiredStation
            }
        }

        @ApiModelProperty(dataType = "date", example = "2017-06-01", position = 10, required = true, value = "Send date", allowableValues = "2017-06-01")
        var sendDate: ShortDate? = null

        @ApiModelProperty(dataType = "date", example = "2017-06-02", position = 20, required = false, value = "Desired delivery date", allowableValues = "2017-06-02")
        var desiredDeliveryDate: ShortDate? = null

        @ApiModelProperty(value = "Sum of DeKu servic values", position = 22, required = false)
        var services: Int? = null

        @ApiModelProperty(value = "Real weight", position = 24, required = false)
        var weight: Float? = null

        @ApiModelProperty(value = "Sender", position = 30, required = false)
        var sender: Participant? = null

        @ApiModelProperty(value = "Consignee", position = 40, required = false)
        var consignee: Participant? = null

        @JvmOverloads constructor(
                sendDate: ShortDate? = null,
                desiredDeliveryDate: ShortDate? = null,
                services: Int? = null,
                weight: Float? = null,
                sender: Participant? = null,
                consignee: Participant? = null) {

            this.sendDate = sendDate
            this.desiredDeliveryDate = desiredDeliveryDate
            this.services = services
            this.weight = weight
            this.sender = sender
            this.consignee = consignee
        }
    }

    /**
     * Routing service request response
     * Created by JT on 23.06.15.
     */
    @ApiModel(value = "Routing", subTypes = [(Routing.Participant::class)], description = "Routing response")
    class Routing() {
        /**
         * Routing service request response member
         * Created by JT on 23.06.15.
         */
        @ApiModel(value = "Participant", description = "Response participant. Sender or consignee")
        class Participant {
            @ApiModelProperty(dataType = "string", example = "020", position = 10, required = true, value = "Station number", allowableValues = "010 - 999")
            var station: Int = 0

            val stationFormatted: String?
                get() = this.station.toString().padStart(3, '0')

            @ApiModelProperty(dataType = "string", example = "PL", position = 20, required = true, value = "Country two-letter ISO-3166")
            var country: String = ""

            @ApiModelProperty(dataType = "string", example = "01-1010", position = 30, required = true, value = "Zipcode contry conform")
            var zipCode: String = ""

            @ApiModelProperty(dataType = "string", example = "A", position = 40, required = true, value = "Zone", allowableValues = "A,B,C,D")
            var zone: String = ""

            @ApiModelProperty(dataType = "string", example = "WR", position = 45, required = false, value = "National zone", allowableValues = "WR,UL")
            var nationalZone: String = ""

            @ApiModelProperty(dataType = "string", example = "X", position = 50, required = true, value = "Sector", allowableValues = "A-Z")
            var sector: String = ""

            @ApiModelProperty(dataType = "string", example = "Workday", position = 60, required = true, value = "Type of day")
            var dayType: String = DayType.Workday.toString()

            @ApiModelProperty(example = "false", position = 70, required = true, value = "Is island")
            var island: Boolean = false

            @ApiModelProperty(dataType = "integer", example = "1", position = 80, required = true, value = "Termtime in days", allowableValues = ">=1")
            var term: Int = 1

            @ApiModelProperty(dataType = "string", example = "08:01", position = 90, required = true, value = "Earliest time of delivery", allowableValues = "00:00 - 23:59")
            var earliestTimeOfDelivery: ShortTime? = ShortTime()

            @ApiModelProperty(dataType = "string", example = "12:00", position = 120, required = true, value = "Delivery time until on saturday", allowableValues = "00:00 - 23:59")
            var saturdayDeliveryUntil: ShortTime = ShortTime(localTime = "00:00")

            @ApiModelProperty(dataType = "string", example = "12:00", position = 130, required = true, value = "Delivery time until on sunday", allowableValues = "00:00 - 23:59")
            var sundayDeliveryUntil: ShortTime = ShortTime(localTime = "00:00")

            @ApiModelProperty(dataType = "string", example = "16:00", position = 140, required = true, value = "Pick up time until", allowableValues = "00:00 - 23:59")
            var pickupUntil: ShortTime = ShortTime()

            @ApiModelProperty(dataType = "string", example = "AH", position = 150, required = true, value = "Partner manager")
            var partnerManager: String = ""

            @ApiModelProperty(hidden = true)
            var date: java.util.Date? = null

            @ApiModelProperty(hidden = true)
            var message: String? = ""
        }

        @ApiModelProperty(position = 30)
        var sender: Participant? = Participant()

        @ApiModelProperty(position = 40)
        var consignee: Participant? = Participant()

        @ApiModelProperty(dataType = "string", example = "F,N", position = 50, required = true, value = "Used via hubs: \"F,N\"")
        var viaHubs: Array<String>? = null

        @ApiModelProperty(dataType = "string", example = "F,N 100", position = 60, required = true, value = "Routing label content:  \"F,N 100\"")
        var labelContent: String = ""

        @ApiModelProperty(dataType = "string", example = "OK", position = 70, required = true, value = "Info message: \"OK\"")
        var message: String = ""

        @ApiModelProperty(dataType = "date", example = "2017-06-01", position = 10, required = true, value = "Send date", allowableValues = "2017-06-01")
        var sendDate: ShortDate? = null

        @ApiModelProperty(dataType = "date", example = "2017-06-02", position = 20, required = false, value = "Delivery date", allowableValues = "2017-06-01")
        var deliveryDate: ShortDate? = null
    }

    @POST
    @Path("/request")
    @ApiOperation(value = "Request routing information")
    @ApiResponses(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class)
    )
    fun request(@ApiParam(value = "Routing request") routingRequest: Request): Routing
}
