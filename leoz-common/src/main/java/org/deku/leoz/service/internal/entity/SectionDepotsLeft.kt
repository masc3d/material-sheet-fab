package org.deku.leoz.service.internal.entity

import io.swagger.annotations.ApiModel

/**
 * Created by helke on 05.04.17.
 */
@ApiModel(value = "SectionDepotsLeft", description = "SectionDepotsLeft")
class SectionDepotsLeft {

    var depotsLeft: List<String?> = listOf(null)

    var scanCount: Int? = null

    constructor() {}
    constructor(listDepotsLeft: List<String?>, countScanned: Int?) {
        this.depotsLeft = listDepotsLeft
        this.scanCount = countScanned
    }
}