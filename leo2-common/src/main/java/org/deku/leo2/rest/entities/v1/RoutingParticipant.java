package org.deku.leo2.rest.entities.v1;

import com.wordnik.swagger.annotations.ApiModelProperty;
import org.deku.leo2.rest.entities.ShortDate;
import org.deku.leo2.rest.entities.ShortTime;

/**
 * Routing service request response member
 * Created by JT on 23.06.15.
 */

public class RoutingParticipant {


    private String mSector = "";
    private String mZone = "";
    private ShortTime mEarliestTimeOfDelivery = new ShortTime();
    private Integer mStation = 0;
    private DayType mDayType = DayType.WorkDay;
    private Boolean mIsland = false;
    //private ShortDate mDelieveryDay = new ShortDate();
    //private ShortDate mPreviousDeliveryDay = new ShortDate();
    private String mZipCode = "";
    private Integer mTerm = 1;
    private ShortTime mSaturdayDeliveryUntil = new ShortTime();
    private ShortTime mSundayDeliveryUntil = new ShortTime();
    private ShortTime mPickupUntil = new ShortTime();
    private String mPartnerManager = "";
    private String mCountry = "";


    public RoutingParticipant() {
    }

    public RoutingParticipant(String sector, String zone, ShortTime earliestTimeOfDelivery, Integer station, DayType daytype, Boolean island,
                              String zipCode, Integer term, ShortTime saturdayDeliveryUntil, ShortTime sundayDeliveryUntil,
                              ShortTime pickupUntil, String partnerManager, String country) {

        mSector = sector;
        mZone = zone;
        mEarliestTimeOfDelivery = earliestTimeOfDelivery;
        mStation = station;
        mDayType = daytype;
        mIsland = island;
        mZipCode = zipCode;
        mTerm = term;
        mSaturdayDeliveryUntil = saturdayDeliveryUntil;
        mSundayDeliveryUntil = sundayDeliveryUntil;
        mPickupUntil = pickupUntil;
        mPartnerManager = partnerManager;
        mCountry = country;

    }

    @ApiModelProperty(dataType = "string", example = "020", position = 10, required = true, value = "Stationnumber: \"020\"")
    public String getStation() {
        return mStation.toString();
    }

    public void setStation(Integer station) {
        mStation = station;
    }

    @ApiModelProperty(dataType = "string", example = "DE", position = 20, required = true, value = "Country two-letter ISO-3166: \"DE\"")
    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    @ApiModelProperty(dataType = "string", example = "01-1010", position = 30, required = true, value = "Zipcode contry conform: \"PL: 01-1010\"")
    public String getZipCode() {
        return mZipCode;
    }

    public void setZipCode(String ZipCode) {
        mZipCode = ZipCode;
    }

    @ApiModelProperty(dataType = "string", example = "WR", position = 40, required = true, value = "specify Zone: \"WR\"", allowableValues = "A,B,C,D,WR,UL")
    public String getZone() {
        return mZone;
    }

    public void setZone(String zone) {
        mZone = zone;
    }

    @ApiModelProperty(dataType = "string", example = "X", position = 50, required = true, value = "specify Sector : \"X\"", allowableValues = "A-Z")
    public String getSector() {
        return mSector;
    }

    public void setSector(String sector) {
        mSector = sector;
    }

    @ApiModelProperty(example = "WorkDay", position = 60, required = true, value = "Type of Day: \"WorkDay\"")
    public DayType getDayType() {
        return mDayType;
    }

    public void setDayType(DayType daytype) {
        mDayType = daytype;
    }

    @ApiModelProperty(example = "false", position = 70, required = true, value = "specify Islands")
    public Boolean getIsland() {
        return mIsland;
    }

    public void setIsland(Boolean island) {
        mIsland = island;
    }


    @ApiModelProperty(dataType = "integer", example = "1", position = 80, required = true, value = "termtime in days: \"1\"", allowableValues = ">=1")
    public Integer getTerm() {
        return mTerm;
    }

    public void setTerm(Integer term) {
        mTerm = term;
    }

    @ApiModelProperty(dataType = "string", example = "08:01", position = 90, required = true, value = "Earliest time of delivery: \"08:01\"", allowableValues = "00:00 - 23:59")
    public ShortTime getEarliestTimeOfDelivery() {
        return mEarliestTimeOfDelivery;
    }

    public void setEarliestTimeOfDelivery(ShortTime earliestTimeOfDelivery) {
        mEarliestTimeOfDelivery = earliestTimeOfDelivery;
    }


//    @ApiModelProperty(dataType = "date", example = "2015-06-01", position = 100, required = true, value = "delivery day regarding holidays and term: \"2015-06-01\"", allowableValues = "00:00 - 23:59")
//    public ShortDate getDelieveryDay() {
//        return mDelieveryDay;
//    }
//
//    public void setDelieveryDay(ShortDate delieveryDay) {
//        mDelieveryDay = delieveryDay;
//    }

//    @ApiModelProperty(hidden = true)
//    public ShortDate getPreviousDeliveryDay() {
//        return mPreviousDeliveryDay;
//    }
//
//    public void setPreviousDeliveryDay(ShortDate PreviousDeliveryDay) {
//        mPreviousDeliveryDay = PreviousDeliveryDay;
//    }


    @ApiModelProperty(dataType = "string", example = "10:00", position = 120, required = true, value = "delivery time until on Saturday: \"08:01\"", allowableValues = "00:00 - 23:59")
    public ShortTime getSaturdayDeliveryUntil() {
        return mSaturdayDeliveryUntil;
    }
    public void setSaturdayDeliveryUntil(ShortTime saturdayDeliveryUntil) {
        mSaturdayDeliveryUntil = saturdayDeliveryUntil;
    }

    @ApiModelProperty(dataType = "string", example = "10:00", position = 130, required = true, value = "delivery time until on Sunday: \"08:01\"", allowableValues = "00:00 - 23:59")
    public ShortTime getSundayDeliveryUntil() {
        return mSundayDeliveryUntil;
    }

    public void setSundayDeliveryUntil(ShortTime SundayDeliveryUntil) {
        mSundayDeliveryUntil = SundayDeliveryUntil;
    }

    @ApiModelProperty(dataType = "string", example = "10:00", position = 140, required = true, value = "pick up time until: \"19:00\"", allowableValues = "00:00 - 23:59")
    public ShortTime getPickupUntil() {
        return mPickupUntil;
    }

    public void setPickupUntil(ShortTime PickupUntil) {
        mPickupUntil = PickupUntil;
    }

    @ApiModelProperty(dataType = "string", example = "MG", position = 150, required = true, value = "Partner Manager: \"AH\"")
    public String getPartnerManager() {
        return mPartnerManager;
    }

    public void setPartnerManager(String PartnerManager) {
        mPartnerManager = PartnerManager;
    }
}
