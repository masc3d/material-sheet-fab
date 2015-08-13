package org.deku.leoz.rest.entities.v1;

/**
 * Created by JT on 02.06.15.
 */
public enum DayType {
//    unknown(0),
    Workday("Workday"),
    Saturday("Saturday"),
    Sunday("Sunday"),
    Holiday("Holiday"),
    RegionalHoliday("RegionalHoliday");

    String mType;

    DayType(String type) {
        mType = type;
    }
}