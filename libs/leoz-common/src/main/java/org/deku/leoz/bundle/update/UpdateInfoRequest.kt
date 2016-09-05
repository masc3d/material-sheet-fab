package org.deku.leoz.bundle.update

import java.io.Serializable

/**
 * Update info request message
 * Created by masc on 12.10.15.
 */
data class UpdateInfoRequest(
        /** Id of the node requesting the update */
        val nodeKey: String = "",
        /** Name of bundle to request update info for */
        val bundleName: String = "",
        /** Optional version alias */
        val versionAlias: String? = null) : Serializable {
    companion object {
        private const val serialVersionUID = -1111506008152543276L
    }
}