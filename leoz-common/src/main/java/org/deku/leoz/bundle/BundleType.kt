package org.deku.leoz.bundle

import sx.io.serialization.Serializable

/**
 * Created by masc on 09.09.15.
 */
@Serializable(0x4a62227da5484b)
enum class BundleType(val value: String) {
    LeozCentral("leoz-central"),
    LeozNode("leoz-node"),
    LeozUI("leoz-ui"),
    LeozBoot("leoz-boot"),
    LeozMobile("leoz-mobile")
}