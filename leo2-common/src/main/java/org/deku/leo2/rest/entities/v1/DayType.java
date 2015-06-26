package org.deku.leo2.rest.entities.v1;

/**
 * Created by JT on 02.06.15.
 */
public enum DayType {
//    unknown(0),
    WorkDay(1),
    Saturday(2),
    Sunday(3),
    Holiday(4),
    RegionalHoliday(5);

    int mType;

    DayType(int type) {
        mType = type;
    }
}