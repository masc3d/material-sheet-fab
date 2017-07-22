package sx.format


/**
 * Extension for double string formatting
 */
fun Double.format(decimalDigits: Int): String {
    return "%.${decimalDigits}f".format(this)
}