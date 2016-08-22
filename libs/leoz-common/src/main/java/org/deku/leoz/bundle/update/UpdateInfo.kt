package org.deku.leoz.bundle.update

import java.io.Serializable
import java.time.LocalTime

/**
 * Update info/notification message.
 * Sent by update providers when new (software) updates become available or as a response to {@link UpdateInfoRequest}
 * @author masc
 */
data class UpdateInfo(
        /** Bundle name */
        val bundleName: String = "",
        /** Bundle version pattern, eg. "2.+" or "+RELEASE" */
        val bundleVersionPattern: String = "",
        /** Desired time for restarting if a bundle is self updating. If omitted the update is supposed to become active asap. */
        val desiredRestartTime: LocalTime? = null,
        /** Latest designated version for this bundle/node. Contains a string referring to {@link Bundle.Version} */
        val latestDesignatedVersion: String? = null,
        /** Plaforms the latest deisgnated version is available for. Contains a list of strings referring to {@link PlatformId} */
        val latestDesignatedVersionPlatforms: Array<String> = arrayOf()
)
: Serializable {
    companion object {
        private const val serialVersionUID = -5708971601187819394L
    }
}
