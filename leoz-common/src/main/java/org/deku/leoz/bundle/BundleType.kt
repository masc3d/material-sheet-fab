package org.deku.leoz.bundle

import sx.io.serialization.Serializable

/**
 * Created by masc on 09.09.15.
 */
@Serializable(0x4a62227da5484b)
enum class BundleType(val value: String) {
    LEOZ_CENTRAL("leoz-central"),
    LEOZ_NODE("leoz-node"),
    LEOZ_UI("leoz-ui"),
    LEOZ_BOOT("leoz-boot"),
    LEOZ_MOBILE("leoz-mobile")
}