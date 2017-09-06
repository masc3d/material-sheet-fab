package org.deku.leoz.model

data class ParcelAdditionalinfo(
        var recipient: String? = null,
        var damagedFileUIDs: List<String>? = null,
        var pictureFileUID: String? = null
)