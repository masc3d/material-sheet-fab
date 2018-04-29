package org.deku.leoz.time

// Utility methods

fun java.time.LocalDateTime.workDate(): java.time.LocalDate =
        this.minusHours(5).toLocalDate()
