package org.deku.leo2.rest.entities.v1;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.deku.leo2.rest.entities.ShortDate;
import org.deku.leo2.rest.entities.ShortTime;

/**
 * Routing service find response
 * Created by masc on 20.04.15.
 */
public class Routing {
    private String mSector = "";
    private String mZone = "";
    private ShortTime mEarliestTimeOfDelivery = new ShortTime();
    private ShortTime mEarliestTimeOfDelivery2 = new ShortTime();
    private Integer mRouting = 0;
    private HolidayType mHoliday = HolidayType.Regular;
    private Boolean mIsland = false;
    private ShortDate mNextDelieveryDay = new ShortDate();
    private ShortDate mPreviousDeliveryDay = new ShortDate();

    public Routing() {
    }

    public Routing(String sector, String zone, ShortTime earliestTimeOfDelivery, Integer routing, HolidayType holiday, Boolean island, ShortDate NextDelieveryDay, ShortDate mPreviousDeliveryDay) {
        mSector = sector;
        mZone = zone;
        mEarliestTimeOfDelivery = earliestTimeOfDelivery;
        mRouting = routing;
        mHoliday = holiday;
        mIsland = island;
    }

    public String getSector() {
        return mSector;
    }

    public void setSector(String sector) {
        mSector = sector;
    }

    public String getZone() {
        return mZone;
    }

    public void setZone(String zone) {
        mZone = zone;
    }

    @ApiModelProperty(dataType = "string", example = "10:00")
    public ShortTime getEarliestTimeOfDelivery() {
        return mEarliestTimeOfDelivery;
    }

    public void setEarliestTimeOfDelivery(ShortTime earliestTimeOfDelivery) {
        mEarliestTimeOfDelivery = earliestTimeOfDelivery;
    }

    @ApiModelProperty(dataType = "string", example = "10:00")
    public ShortTime getEarliestTimeOfDelivery2() {
        return mEarliestTimeOfDelivery2;
    }

    public void setEarliestTimeOfDelivery2(ShortTime earliestTimeOfDelivery2) {
        mEarliestTimeOfDelivery2 = earliestTimeOfDelivery2;
    }

    public Integer getRouting() {
        return mRouting;
    }

    public void setRouting(Integer routing) {
        mRouting = routing;
    }

    public HolidayType getHoliday() {
        return mHoliday;
    }

    public void setHoliday(HolidayType holiday) {
        mHoliday = holiday;
    }

    public Boolean getIsland() {
        return mIsland;
    }

    public void setIsland(Boolean island) {
        mIsland = island;
    }

    @ApiModelProperty(dataType = "date", example = "2015-01-01")
    public ShortDate getNextDelieveryDay() {
        return mNextDelieveryDay;
    }

    public void setNextDelieveryDay(ShortDate NextDelieveryDay) {
        mNextDelieveryDay = NextDelieveryDay;
    }

    @ApiModelProperty(dataType = "date", example = "2015-01-01")
    public ShortDate getPreviousDeliveryDay() {
        return mPreviousDeliveryDay;
    }

    public void setPreviousDeliveryDay(ShortDate PreviousDeliveryDay) {
        mPreviousDeliveryDay= PreviousDeliveryDay;
    }
}
