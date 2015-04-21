package org.deku.leo2.rest.entities.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by masc on 20.04.15.
 */
@XmlType
@XmlEnum(Integer.class)
public enum HolidayType {
    @XmlEnumValue("1") Regular(1),
    @XmlEnumValue("2") RegionalBankHoliday(2),
    @XmlEnumValue("3") BankHoliday(3),
    @XmlEnumValue("4") Saturday(4),
    @XmlEnumValue("5") Sunday(5);

    int mType;

    HolidayType(int type) {
        mType = type;
    }
}
