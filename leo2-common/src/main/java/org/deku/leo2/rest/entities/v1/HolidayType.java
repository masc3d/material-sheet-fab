package org.deku.leo2.rest.entities.v1;

/**
 * Created by masc on 20.04.15.
 */
public enum HolidayType {
    Regular(1),
    RegionalBankHoliday(2),
    BankHoliday(3),
    Saturday(4),
    Sunday(5);

    int mType;

    HolidayType(int type) {
        mType = type;
    }
}
