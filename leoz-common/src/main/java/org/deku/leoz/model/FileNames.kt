package org.deku.leoz.model

import sx.time.plusMinutes
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*


enum class Location {
    HUB,
    SB,
    SB_Original,
    RK,
    QubeVu
}

class FileName(val value: String, val date: Date, val type: Location, val basePath: Path, val additionalInfo: String? = null) {


    fun getPath(): Path {
        val workdate: Date = when (type) {
            Location.QubeVu, Location.HUB -> date.plusMinutes(-360)
            else -> date
        }


        val relPathMobile = basePath.resolve(SimpleDateFormat("yyyy").format(workdate))
                .resolve(type.toString())
                .resolve(SimpleDateFormat("MM").format(workdate))
                .resolve(SimpleDateFormat("dd").format(workdate))
                .toFile()
        relPathMobile.mkdirs()

        return relPathMobile.toPath()

    }

    fun getFilenameWithoutExtension(): String {
        val addInfo = additionalInfo ?: ""
        val filenameWithoutExtension = when (type) {
            Location.SB, Location.SB_Original -> value + "_" + addInfo + "_" + SimpleDateFormat("yyyyMMddHHmmssSSS").format(date) + "_MOB"
            Location.HUB -> value //line eg. 1010.bmp
            Location.QubeVu -> addInfo + "_" + value + SimpleDateFormat("yyyyMMddHHmmss").format(date) + "QVT"  //addInfo="Q_IP[3]" eg "Q_242"
            Location.RK -> "RK_" + value + "_" + addInfo + "_" + SimpleDateFormat("yyyyMMddHHmmssSSS").format(date)
        }

        return filenameWithoutExtension
    }
}