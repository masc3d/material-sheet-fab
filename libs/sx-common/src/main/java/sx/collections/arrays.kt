package sx.collections

/**
 * Split a byte array into chunks
 * TODO: may become obsolete with kt-1.2 which has this by default
 */
fun ByteArray.chunked(maxSize: Int): List<ByteArray> {
    val fullChunks = this.size / maxSize
    val remainder = this.size % maxSize

    return 0.until(fullChunks)
            .asSequence()
            // Map to full chunk int ranges
            .map {
                val i = it.times(maxSize)
                i.until(i + maxSize)
            }

            .let {
                // Add remainder chunk (could be empty)
                when {
                    remainder > 0 -> it.plus<IntRange>({
                        val i = fullChunks * maxSize
                        i.until(i + remainder)
                    }())
                    else -> it
                }
            }

            // Slice array
            .map {
                this.sliceArray(it)
            }
            .toList()
}