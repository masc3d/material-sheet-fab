package org.deku.leoz.bundle

import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import java.io.File
import java.util.*

/**
 * Bundle installer for installing and updating bundles locally
 * Created by masc on 17.09.15.
 */
class BundleInstaller(
        /** Path containing bundles */
        public val bundleContainerPath: File,
        /** Remote bundle repository. The bundle name of this repository has to match the installer name */
        public val repository: BundleRepository) {

    private val log = LogFactory.getLog(this.javaClass)

    companion object {
        private val UPDATE_SUFFIX = ".update"
        private val DOWNLOAD_SUFFIX = ".download"
        private val OLD_SUFFIX = ".old"

        /**
         * Checks if (file/folder) name is a valid module name
         */
        private fun isBundleName(name: String): Boolean {
            return !name.endsWith(UPDATE_SUFFIX) &&
                    !name.endsWith(OLD_SUFFIX) &&
                    !name.endsWith(DOWNLOAD_SUFFIX)
        }

        /**
         * Gets the bundle path ready for use with the Bundle class
         * This path is plaform specific, as on some platform the bundle resides in a subfolder
         * (eg. on OSX ${bundleName}.app)
         */
        fun getNativeBundlePath(bundlePath: File): File {
            return if (SystemUtils.IS_OS_MAC_OSX)
                File(bundlePath, "${bundlePath.name}.app")
            else
                bundlePath
        }
    }

    init {
    }

    /**
     * Creates bundle update path
     * @param bundleName Bundle name
     */
    private fun bundleUpdatePath(bundleName: String): File {
        return File(bundleContainerPath, "${bundleName}${UPDATE_SUFFIX}")
    }

    /**
     * Creates bundle download path
     * @param bundleName Bundle name
     */
    private fun bundleDownloadPath(bundleName: String): File {
        return File(bundleContainerPath, "${bundleName}${DOWNLOAD_SUFFIX}")
    }

    /**
     * Creates bundle path
     * @param bundleName Bundle name
     */
    private fun bundlePath(bundleName: String): File {
        return File(bundleContainerPath, bundleName)
    }

    /**
     * Create native bundle path
     * @param bundleName Bundle name
     */
    private fun nativeBundlePath(bundleName: String): File {
        return getNativeBundlePath(this.bundlePath(bundleName))
    }

    /**
     * Creates path for old bundle (used when moving into place)
     * @param bundleName Bundle name
     */
    private fun oldBundlePath(bundleName: String): File {
        return File(bundleContainerPath, "${bundleName}${OLD_SUFFIX}")
    }

    /**
     * Current bundle
     * @param bundleName Bundle name
     */
    public fun bundle(bundleName: String): Bundle {
        return Bundle(getNativeBundlePath(this.bundlePath(bundleName)), bundleName)
    }

    /**
     * Indicates if bundle already exists
     * @param bundleName Bundle name
     */
    fun hasBundle(bundleName: String): Boolean {
        return this.bundlePath(bundleName).exists()
    }

    /**
     * Indicates if there's an update for a bundle
     * @param bundleName Bundle name
     */
    fun hasUpdate(bundleName: String): Boolean {
        return this.bundleUpdatePath(bundleName).exists()
    }

    /**
     * Download specific bundle version as bundle update (not overwriting the current bundle)
     * @param bundleName Bundle name
     * @param version Bundle version
     */
    private fun download(bundleName: String,
                         version: Bundle.Version,
                         onProgress: ((file: String, percentage: Double) -> Unit)? = null) {

        val updatePath = this.bundleUpdatePath(bundleName)
        val downloadPath = this.bundleDownloadPath(bundleName)

        // If an update is already in place, make it the current download path, possibly minimizing download time
        if (updatePath.exists()) {
            if (downloadPath.exists())
                downloadPath.deleteRecursively()

            updatePath.renameTo(downloadPath)
        }

        val platform = PlatformId.current()
        var copyPaths = this.listBundlePaths()
        if (platform.operatingSystem == OperatingSystem.OSX)
            copyPaths = copyPaths.map { f -> File(f, "${f.name}.app") }

        repository.download(
                bundleName,
                version,
                platform,
                destPath = downloadPath,
                copyPaths = copyPaths,
                verify = true,
                onProgress = { f, p ->
                    if (onProgress != null) onProgress(f, p)
                })

        downloadPath.renameTo(updatePath)
    }

    /**
     * Applies prepared update. Moves an prepared update into place.
     * @param bundleName Bundle name
     * @return If any changes were applied (successfully)
     */
    private fun applyUpdate(bundleName: String): Boolean {
        var changesApplied = false
        val bundlePath = this.bundlePath(bundleName)
        val updatePath = this.bundleUpdatePath(bundleName)

        if (bundlePath.exists()) {
            if (updatePath.exists()) {
                val oldBundlePath = this.oldBundlePath(bundleName)
                if (oldBundlePath.exists()) {
                    log.info("Removing old bundle path [${oldBundlePath}]")
                    oldBundlePath.deleteRecursively()
                }
                log.info("Moving update into place [${updatePath}] -> [${bundlePath}]")
                bundlePath.renameTo(oldBundlePath)
                try {
                    updatePath.renameTo(bundlePath)
                } catch(e: Exception) {
                    oldBundlePath.renameTo(bundlePath)
                    throw e
                }
                oldBundlePath.deleteRecursively()
                changesApplied = true
            }
        } else {
            log.warn("Bundle named [${bundleName}] doesn't exist within [${this.bundleContainerPath}]")
        }
        return changesApplied
    }

    /**
     * List bundle container paths. The bundle itself may reside in a subfolder (eg. on OSX it's the .app osx bundle)
     */
    fun listBundlePaths(): List<File> {
        return this.bundleContainerPath.listFiles { f -> f.isDirectory && isBundleName(f.name) }?.asList() ?: ArrayList()
    }

    /**
     * List bundle names
     */
    fun listBundleNames(): List<String> {
        return this.listBundlePaths().map { f -> f.name }
    }

    /**
     * Cleans out any existing metadirs (download, update, old..) for this bundle
     */
    private fun clean(bundleName: String) {
        log.info("Cleaning bundle metadirs for [${bundleName}]")
        val paths = arrayOf(
                this.bundleUpdatePath(bundleName),
                this.bundleDownloadPath(bundleName),
                this.oldBundlePath(bundleName))

        paths.forEach { p -> p.deleteRecursively() }
    }

    enum class InstallationResult {
        Installed,
        Unchanged
    }

    /**
     * Install bundle from remote repository, also taking care of applying a prepared update and
     * performing native installation by calling into the bundle process' native entry points
     * @param bundleName Name of bundle to install
     * @param version Version to download and install. If not provided, only prepared updates will be applied
     * @param downloadOnly Version will be downloaded as an update, but not installed
     * @param omitNativeInstallation Do not call into bundle process for native stop/start/install/uninstall. This is merely
     * an optimization for bundles which do not require those entry points (eg. leoz-boot)
     * @param onProgress Progress callback
     * @return If changes have been applied
     */
    fun install(
            bundleName: String,
            version: Bundle.Version? = null,
            downloadOnly: Boolean,
            omitNativeInstallation: Boolean = false,
            onProgress: ((file: String, percentage: Double) -> Unit)? = null): Boolean {

        var changesApplied = false

        var bundle: Bundle? = null
        if (this.hasBundle(bundleName)) {
            // Try to load existing bundle
            try {
                bundle = Bundle.load(
                        BundleInstaller.getNativeBundlePath(
                                File(this.bundleContainerPath, bundleName)))

                log.info("Current bundle [${bundle}")

            } catch(e: Exception) {
                this.log.error(e.message, e)
            }
        }

        // Stop and uninstall native bundle process if applicable
        if (!downloadOnly && !omitNativeInstallation && bundle != null) {
            bundle.stop()
            bundle.uninstall()
        }

        if (version != null) {
            if (bundle != null && version.equals(bundle.version)) {
                log.info("Version [${version}] already installed")
                this.clean(bundleName)
            } else {
                org.deku.leoz.bundle.log.info("Installing [${bundleName}-${version}]")

                this.download(
                        bundleName,
                        version,
                        onProgress)

                changesApplied = true
            }
        }

        if (!downloadOnly) {
            changesApplied = this.applyUpdate(bundleName)
        }

        if (!downloadOnly && !omitNativeInstallation) {
            // Create a stub bundle instance if needed
            if (bundle == null)
                bundle = this.bundle(bundleName)

            bundle.install()
            bundle.start()
        }

        org.deku.leoz.bundle.log.info("Installed sucessfully.")

        return changesApplied
    }

    /**
     * Install bundle from remote repository, also taking care of applying a prepared update and
     * performing native installation by calling into the bundle process' native entry points
     * @param bundleName Name of bundle to install
     * @param versionPattern Version pattern to match against versions available in repository
     * @param downloadOnly Version will be downloaded as an update (prepared for installation), but not moved into place
     * @param omitNativeInstallation Do not call into bundle process for native stop/start/install/uninstall. This is merely
     * an optimization for bundles which do not require those entry points (eg. leoz-boot)
     * @param onProgress Progress callback
     */
    fun install(bundleName: String,
                versionPattern: String? = null,
                downloadOnly: Boolean,
                omitNativeInstallation: Boolean = false,
                onProgress: ((file: String, percentage: Double) -> Unit)? = null): Boolean {

        var latestMatchingVersion: Bundle.Version? = null
        if (versionPattern != null) {
            val availableVersions = this.repository
                    .listVersions(bundleName)

            org.deku.leoz.bundle.log.info("Repository [${this.repository} has following versions of [${bundleName}]: ${availableVersions.map { it -> it.toString() }.joinToString(", ")}")

            latestMatchingVersion = availableVersions.filter(versionPattern)
                    .sortedDescending()
                    .firstOrNull()

            if (latestMatchingVersion == null)
                throw IllegalArgumentException("No version matching [${versionPattern}] ")
        }

        return this.install(
                bundleName = bundleName,
                version = latestMatchingVersion,
                downloadOnly = downloadOnly,
                omitNativeInstallation = omitNativeInstallation,
                onProgress = onProgress)
    }
}