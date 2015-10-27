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
     * Try to load bundle from bundle container subdir
     * @param bundlePath Bundle path
     */
    private fun tryLoadBundle(bundlePath: File): Bundle? {
        if (bundlePath.exists()) {
            try {
                return Bundle.load(getNativeBundlePath(bundlePath))
            } catch(e: Exception) {
                this.log.error(e.message, e)
            }
        }
        return null
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

    /**
     * Download specific bundle version as bundle update (not overwriting the current bundle)
     * @param bundleName Bundle name
     * @param version Bundle version
     * @param forceDownload Always download, even if existing version is the same
     * @param onProgress Progress callback
     */
    fun download(bundleName: String,
                 version: Bundle.Version,
                 forceDownload: Boolean = false,
                 onProgress: ((file: String, percentage: Double) -> Unit)? = null): Boolean {

        var changesApplied = false

        val bundlePath = this.bundlePath(bundleName)
        val bundleUpdatePath = this.bundleUpdatePath(bundleName)
        val bundleDownloadPath = this.bundleDownloadPath(bundleName)

        var bundle: Bundle? = this.tryLoadBundle(bundlePath)
        var updateBundle: Bundle? = this.tryLoadBundle(bundleUpdatePath)

        if (bundle != null) log.info("Current bundle [${bundle}]")
        if (updateBundle != null) log.info("Current update bundle [${updateBundle}]")

        val bundleUpToDate =
                (bundle != null && version.equals(bundle.version)) ||
                        (updateBundle != null && version.equals(updateBundle.version))

        if (!bundleUpToDate || forceDownload) {
            // If an update is already in place, make it the current download path, possibly minimizing download time
            if (bundleUpdatePath.exists()) {
                if (bundleDownloadPath.exists())
                    bundleDownloadPath.deleteRecursively()

                bundleUpdatePath.renameTo(bundleDownloadPath)
            }

            val platform = PlatformId.current()
            var copyPaths = this.listBundlePaths()
            if (platform.operatingSystem == OperatingSystem.OSX)
                copyPaths = copyPaths.map { f -> File(f, "${f.name}.app") }

            repository.download(
                    bundleName,
                    version,
                    platform,
                    destPath = bundleDownloadPath,
                    copyPaths = copyPaths,
                    verify = true,
                    onProgress = { f, p ->
                        if (onProgress != null) onProgress(f, p)
                    })

            bundleDownloadPath.renameTo(bundleUpdatePath)

            changesApplied = true
        } else {
            log.info("Version [${version}] already downloaded")
        }
        return changesApplied
    }

    /**
     * Download specific bundle version as bundle update (not overwriting the current bundle)
     * @param bundleName Bundle name
     * @param version Bundle version
     * @param forceDownload Always download, even if existing version is the same
     * @param onProgress Progress callback
     */
    fun download(bundleName: String,
                 versionPattern: String,
                 forceDownload: Boolean = false,
                 onProgress: ((file: String, percentage: Double) -> Unit)? = null): Boolean {

        log.info("Checking repository for version matching [${versionPattern}]")

        var latestMatchingVersion: Bundle.Version? = null
        val availableVersions = this.repository
                .listVersions(bundleName)

        log.info("Repository [${this.repository} versions [${bundleName}]: ${availableVersions.map { it -> it.toString() }.joinToString(", ")}")

        latestMatchingVersion = availableVersions.filter(versionPattern)
                .sortedDescending()
                .firstOrNull()

        if (latestMatchingVersion == null)
            throw IllegalArgumentException("No version matching [${versionPattern}]")

        return this.download(bundleName = bundleName,
                version = latestMatchingVersion,
                forceDownload = forceDownload,
                onProgress = onProgress)
    }

    /**
     * Installs previously downloaded bundle
     * performing native installation by calling into the bundle process' native entry points
     * @param bundleName Name of bundle to install
     * @param omitNativeInstallation Do not call into bundle process for native stop/start/install/uninstall. This is merely
     * an optimization for bundles which do not require those entry points (eg. leoz-boot)
     * @return If changes have been applied
     */
    fun install(
            bundleName: String,
            omitNativeInstallation: Boolean = false) {

        log.info("Installing [${bundleName}]")

        val bundlePath = this.bundlePath(bundleName)
        val bundleUpdatePath = this.bundleUpdatePath(bundleName)

        var bundle: Bundle? = this.tryLoadBundle(bundlePath)
        val updateBundle: Bundle? = this.tryLoadBundle(bundleUpdatePath)

        // Stop and uninstall native bundle process if applicable
        if (!omitNativeInstallation && bundle != null) {
            bundle.stop()
            bundle.uninstall()
        }

        if (bundleUpdatePath.exists()) {
            log.info("Updating to [${updateBundle}]")

            val oldBundlePath = this.oldBundlePath(bundleName)
            if (oldBundlePath.exists()) {
                log.info("Removing backup bundle path [${oldBundlePath}]")
                oldBundlePath.deleteRecursively()
            }

            bundlePath.renameTo(oldBundlePath)
            try {
                bundleUpdatePath.renameTo(bundlePath)
            } catch(e: Exception) {
                oldBundlePath.renameTo(bundlePath)
                throw e
            }
            oldBundlePath.deleteRecursively()
        }

        if (!omitNativeInstallation) {
            // Create a stub bundle instance if needed
            if (bundle == null)
                bundle = this.bundle(bundleName)

            bundle.install()
            bundle.start()
        }

        log.info("Installed sucessfully.")
    }

    /**
     * Uninstall bundle
     * @param bundleName Bundle name
     */
    fun uninstall(bundleName: String) {
        log.info("Uninstalling [${bundleName}]")

        val bundlePath = this.bundlePath(bundleName)

        if (bundlePath.exists()) {
            val bundle = this.bundle(bundleName)
            bundle.stop()
            bundle.uninstall()
            log.info("Uninstalled sucessfully.")
        } else {
            log.warn("Bundle does not exist.")
        }
    }
}