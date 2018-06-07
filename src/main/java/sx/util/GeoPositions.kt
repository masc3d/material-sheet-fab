package sx.util

fun getGeoPositionsDistance(lonFirst: Double, latFirst: Double, lonSecond: Double, latSecond: Double): Double? {
    try {
        if (lonSecond == 0.0 && latSecond == 0.0)
            return null

        val tLat = (latSecond - latFirst) * Math.PI / 180
        val tLon = (lonSecond - lonFirst) * Math.PI / 180
        val oLat = latFirst * Math.PI / 180
        val oLastLat = latSecond * Math.PI / 180
        val a = Math.pow(Math.sin(tLat / 2), 2.0) + Math.pow(Math.sin(tLon / 2), 2.0) * Math.cos(oLat) * Math.cos(oLastLat)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return 6371 * c


    } catch (e: Exception) {
        return null
    }
}

