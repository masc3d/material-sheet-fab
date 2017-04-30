package org.deku.leoz.service.entity.pub

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.entity.ShortTime

/**
 * Routing service request response
 * Created by JT on 23.06.15.
 */
@ApiModel(value = "Routing", subTypes = arrayOf(Routing.Participant::class), description = "Routing response")
class Routing {
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

        @ApiModelProperty(dataType = "string", example = "WR", position = 40, required = true, value = "Zone", allowableValues = "A,B,C,D,WR,UL")
        var zone: String = ""

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
        var saturdayDeliveryUntil: ShortTime = ShortTime()

        @ApiModelProperty(dataType = "string", example = "12:00", position = 130, required = true, value = "Delivery time until on sunday", allowableValues = "00:00 - 23:59")
        var sundayDeliveryUntil: ShortTime = ShortTime()

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

    @ApiModelProperty(dataType = "date", example = "2015-06-01", position = 10, required = true, value = "Send date", allowableValues = "2015-06-01")
    var sendDate: ShortDate? = null

    @ApiModelProperty(dataType = "date", example = "2015-06-02", position = 20, required = false, value = "Delivery date", allowableValues = "2015-06-01")
    var deliveryDate: ShortDate? = null

    constructor() {
    }
}
