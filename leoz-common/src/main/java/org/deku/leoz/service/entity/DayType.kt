package org.deku.leoz.service.entity

/**
 * Created by JT on 02.06.15.
 */
enum class DayType private constructor(private var type: String) {
    Workday("Workday"),
    Saturday("Saturday"),
    Sunday("Sunday"),
    Holiday("Holiday"),
    RegionalHoliday("RegionalHoliday")
}

enum class DayTypeKey(val value:Int){
    WORKDAY(1),
    SATURDAY(2),
    SUNDAY(3),
    HOLIDAY(4),
    REGIONALHOLIDAY(4)
}