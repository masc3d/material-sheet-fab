package sx.android.anko.sqlite

// Some map extensions for extracting data from sqlite row maps

fun Map<String, Any?>.getInt(key: String): Int {
    return (this.get(key) as Long).toInt()
}

fun Map<String, Any?>.getBoolean(key: String): Boolean {
    return when (this.get(key) as Long) { 0L -> false; else -> true }
}

fun Map<String, Any?>.getString(key: String): String {
    return this.get(key) as String
}

fun Map<String, Any?>.getByteArray(key: String): ByteArray {
    return this.get(key) as ByteArray
}