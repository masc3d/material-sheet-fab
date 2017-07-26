package org.deku.leoz.mobile.dev

import sx.aidc.SymbologyType

/**
 * Collection of synthetic inputs
 */
class SyntheticInputs(
        val name: String,
        val entries: List<Entry>) {

    /**
     * Created by masc on 26.07.17.
     */
    data class Entry(
            val data: String,
            val symbologyType: SymbologyType
    )

}
