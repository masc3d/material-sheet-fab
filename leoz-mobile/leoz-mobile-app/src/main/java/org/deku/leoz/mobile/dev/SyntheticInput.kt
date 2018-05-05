package org.deku.leoz.mobile.dev

import sx.aidc.SymbologyType

/**
 * Collection of synthetic inputs
 */
class SyntheticInput(
        /** Name of this input type */
        val name: String,
        /** Input entries */
        val entries: List<Entry>,
        /** Multiple choice input */
        val multipleChoice: Boolean = false,
        /** Performs a monkey test, repeating, shuffling and emitting all inputs at the same time */
        val monkeyRepetitions: Int = 0
) {

    /**
     * Created by masc on 26.07.17.
     */
    data class Entry(
            val data: String,
            val name: String = data,
            val symbologyType: SymbologyType
    )
}
