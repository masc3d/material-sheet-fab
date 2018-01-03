package org.deku.leoz.service.internal.entity.update

import sx.io.serialization.Serializable

/**
 * Update info/notification message.
 * Sent by update providers when new (software) updates become available or as a response to {@link UpdateInfoRequest}
 * @author masc
 */
@Serializable(0x16577d9fec6724)
data class UpdateInfo(
        /** Bundle name */
        val bundleName: String = "",
        /** Bundle version pattern, eg. "2.+" or "+RELEASE" */
        val bundleVersionPattern: String = "",
        /** Bundle version alias */
        val bundleVersionAlias: String = "",
        /** Desired time for restarting if a bundle is self updating. If omitted the update is supposed to become active asap. */
        val desiredRestartTime: java.util.Date? = null,
        /** Latest designated version for this bundle/node. Contains a string referring to {@link Bundle.Version} */
        val latestDesignatedVersion: String? = null,
        /** Plaforms the latest deisgnated version is available for. Contains a list of strings referring to {@link PlatformId} */
        val latestDesignatedVersionPlatforms: Array<String> = arrayOf()
)