package sx.util

import java.util.*

// Random extension methods

/**
 * Create random number with definitive range
 */
fun Random.nextInt(from: Int, to: Int): Int = this.nextInt(to - from) + from
