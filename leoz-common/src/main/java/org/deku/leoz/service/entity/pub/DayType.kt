package org.deku.leoz.service.entity.pub

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