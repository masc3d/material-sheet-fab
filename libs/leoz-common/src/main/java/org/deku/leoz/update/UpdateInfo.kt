package org.deku.leoz.update

/**
 * Update info/notification message.
 * Sent by update providers when new (software) updates become available or as a response to {@link UpdateInfoRequest}
 * @author masc
 */
data class UpdateInfo(
        /** Bundle name */
        val bundleName: String,
        /** Bundle version */
        val bundleVersion: String) {
    companion object {
        private const val serialVersionUID = -5708971601187819394L
    }
}
