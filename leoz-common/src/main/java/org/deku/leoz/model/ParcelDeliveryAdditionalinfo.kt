package org.deku.leoz.model

data class ParcelDeliveryAdditionalinfo(
        var recipient: String? = null,
        var damagedFileUIDs: List<String>? = null,
        var pictureFileUID: String? = null,
        var picturePath: String? = null
)