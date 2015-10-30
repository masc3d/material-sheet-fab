package org.deku.leoz.update

/**
 * Update info request message
 * Created by masc on 12.10.15.
 */
data class UpdateInfoRequest(
        /** Id of the node requesting the update */
        val nodeId: Int = 0,
        /** Name of bundle to request update info for */
        val bundleName: String = "") {
    companion object {
        private const val serialVersionUID = -1111506008152543276L
    }
}