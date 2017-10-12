package org.deku.leoz.mobile.dev

import sx.aidc.SymbologyType

/**
 * Collection of synthetic inputs
 */
class SyntheticInput(
        val name: String,
        val entries: List<Entry>,
        val multipleChoice: Boolean = false
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
