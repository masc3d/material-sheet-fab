package org.deku.leoz.service.internal.entity.update

import sx.io.serialization.Serializable

/**
 * Update info request message
 * Created by masc on 12.10.15.
 */
@Serializable(0xba79258f8c02ab)
data class UpdateInfoRequest(
        /** Id of the node requesting the update */
        val nodeKey: String = "",
        /** Name of bundle to request update info for */
        val bundleName: String = "",
        /** Optional version alias */
        val versionAlias: String = "")