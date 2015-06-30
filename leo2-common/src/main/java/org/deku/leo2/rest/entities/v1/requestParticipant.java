package org.deku.leo2.rest.entities.v1;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.deku.leo2.rest.entities.ShortDate;

import java.util.Date;

/**
 * Sender or consignee attributes
 * Created by masc on 23.06.15.
 */
@ApiModel(description = "requestParticipant in transaction, delivery or consignee")
public class requestParticipant {
    //private ShortDate mDate;
    private String mTimeFrom;
    private String mTimeTo;
    private String mCountry;
    private String mZip;
    private String mStationByRequest;

    public requestParticipant() {
    }

    @ApiModelProperty(dataType = "string", example = "DE", value = "Country two-letter ISO-3166", position = 10, required = true)
    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    @ApiModelProperty(dataType = "string", example = "36286", value = "Zip code accordant to country spezification", position = 20, required = true)
    public String getZip() {
        return mZip;
    }

    public void setZip(String zip) {
        mZip = zip;
    }


    @ApiModelProperty(dataType = "string", example = "09:00", position = 40, required = true, value = "Time window (from)", allowableValues = "00:00 - 23:59")
    public String getTimeFrom() {
        return mTimeFrom;
    }

    public void setTimeFrom(String timefrom) {
        mTimeFrom = timefrom;
    }

    @ApiModelProperty(dataType = "string", example = "12:00", position = 50, required = true, value = "Time window (to)", allowableValues = "00:00 - 23:59")
    public String getTimeTo() {
        return mTimeTo;
    }

    public void setTimeTo(String timeto) {
        mTimeTo = timeto;
    }

    @ApiModelProperty(dataType = "string", example = "020", position = 60, required = true, value = "Stationnumber", allowableValues = "010 - 999")
    public String getStationByRequest() {
        return mStationByRequest;
    }

    public void setStationByRequest(String stationByRequest) {
        mStationByRequest = stationByRequest;
    }

    public requestParticipant(String timeFrom, String timeTo, String country, String zip) {
        //mDate = date;
        mTimeFrom = timeFrom;
        mTimeTo = timeTo;
        mCountry = country;
        mZip = zip;
    }
}
