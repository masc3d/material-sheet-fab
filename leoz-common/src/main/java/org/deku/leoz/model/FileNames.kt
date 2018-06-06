package org.deku.leoz.model

import org.threeten.bp.format.DateTimeFormatter
import sx.time.plusMinutes
import sx.time.threeten.toLocalDateTime
import java.nio.file.Path
import java.util.*


enum class Location(val value: String) {
    HUB("HUB"),
    SB("SB"),
    SB_Original("SB_Original"),
    RK("RK"),
    QubeVu("QubeVu")
}

class FileName(val value: String, val date: Date, val type: Location, val basePath: Path, val additionalInfo: String? = null) {
    val formatterYYYY = DateTimeFormatter.ofPattern("yyyy")
    val formatterMM = DateTimeFormatter.ofPattern("MM")
    val formatterDD = DateTimeFormatter.ofPattern("dd")
    val formatterYMDHS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    val formatterYMDHSS = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")

    fun getPath(): Path {
        val workdate: Date = when (type) {
            Location.QubeVu, Location.HUB -> date.plusMinutes(-360)
            else -> date
        }

        val workdateToLocal = workdate.toLocalDateTime()
        val relPathMobile = basePath.resolve(formatterYYYY.format(workdateToLocal))
                .resolve(type.toString())
                .resolve(formatterMM.format(workdateToLocal))
                .resolve(formatterDD.format(workdateToLocal))
                .toFile()
        relPathMobile.mkdirs()

        return relPathMobile.toPath()

    }

    fun getFilenameWithoutExtension(): String {
        val addInfo = additionalInfo ?: ""
        val filenameWithoutExtension = when (type) {
            Location.SB, Location.SB_Original -> value//"${value}_${addInfo}_${SimpleDateFormat("yyyyMMddHHmmssSSS").format(date)}_MOB"
            Location.HUB -> value //line eg. 1010.bmp
            Location.QubeVu -> "${addInfo}_${value}_${formatterYMDHS.format(date.toLocalDateTime())}QVT"  //addInfo="Q_IP[3]" eg "Q_242"
            Location.RK -> "RK_${value}_${addInfo}_${formatterYMDHSS.format(date.toLocalDateTime())}"
        }

        return filenameWithoutExtension
    }
}