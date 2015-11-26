package org.deku.leoz.bundle

import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import java.io.File
import java.nio.file.Files
import java.util.*

/**
 * Bundle installer for installing and updating bundles locally
 * Created by masc on 17.09.15.
 */
class BundleInstaller(
        /** Path containing bundles */
        public val bundleContainerPath: File) {

    private val log = LogFactory.getLog(this.javaClass)

    companion object {
        private val READY_SUFFIX = ".ready"
        private val DOWNLOAD_SUFFIX = ".download"
        private val OLD_SUFFIX = ".old"

        /**
         * Checks if (file/folder) name is a valid module name
         */
        private fun isBundleName(name: String): Boolean {
            return !name.endsWith(READY_SUFFIX) &&
                    !name.endsWith(OLD_SUFFIX) &&
                    !name.endsWith(DOWNLOAD_SUFFIX)
        }

        /**
         * Gets the bundle path ready for use with the Bundle class
         * This path is plaform specific, as on some platform the bundle resides in a subfolder
         * (eg. on OSX ${bundleName}.app)
         * @param bundlePath Bundle base path
         * @param bundleName Optional bundle name, hinting for native path. If not provided the bundle base path
         * name is expected to be the same as the bundle name
         */
        fun getNativeBundlePath(
                bundlePath: File,
                bundleName: String? = null): File {
            return if (SystemUtils.IS_OS_MAC_OSX)
                File(bundlePath, "${bundleName ?: bundlePath.name}.app")
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
    private fun bundleReadyPath(bundleName: String): File {
        return File(bundleContainerPath, "${bundleName}${READY_SUFFIX}")
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
        return this.bundleReadyPath(bundleName).exists()
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
     * @param bundleName Bundle name. As the bundle name cannot necessarily be derived from the path (suffixes may
     * be in place), the name has to be provided explicitly
     */
    private fun tryLoadBundle(bundlePath: File, bundleName: String): Bundle? {
        if (bundlePath.exists()) {
            try {
                return Bundle.load(getNativeBundlePath(bundlePath, bundleName))
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
                this.bundleReadyPath(bundleName),
                this.bundleDownloadPath(bundleName),
                this.oldBundlePath(bundleName))

        paths.forEach { p -> p.deleteRecursively() }
    }

    /**
     * Download specific bundle version and prepare it as an update, ready for installation
     * @param bundleRepository Bundle repository to download from
     * @param bundleName Bundle name
     * @param version Bundle version
     * @param forceDownload Always download, even if existing version is the same
     * @param onProgress Progress callback
     * @return true if the update is ready to install, false if the currently installed bundle is already uptodate.
     */
    fun download(bundleRepository: BundleRepository,
                 bundleName: String,
                 version: Bundle.Version,
                 forceDownload: Boolean = false,
                 onProgress: ((file: String, percentage: Double) -> Unit)? = null): Boolean {

        var readyToInstall: Boolean

        val bundlePath = this.bundlePath(bundleName)
        val bundleUpdatePath = this.bundleReadyPath(bundleName)
        val bundleDownloadPath = this.bundleDownloadPath(bundleName)

        var bundle: Bundle? = this.tryLoadBundle(bundlePath, bundleName)
        var readyBundle: Bundle? = this.tryLoadBundle(bundleUpdatePath, bundleName)

        if (bundle != null) log.info("Currently installed bundle [${bundle}]")
        if (readyBundle != null) log.info("Currently ready bundle [${readyBundle}]")

        val installedBundleUpToDate = (bundle != null && version.equals(bundle.version))
        val readyBundleUpToDate = (readyBundle != null && version.equals(readyBundle.version))

        if (!(installedBundleUpToDate || readyBundleUpToDate) || forceDownload) {
            // If an update is already in place, make it the current download path, possibly minimizing download time
            if (bundleUpdatePath.exists()) {
                if (bundleDownloadPath.exists())
                    bundleDownloadPath.deleteRecursively()

                Files.move(bundleUpdatePath.toPath(), bundleDownloadPath.toPath())
            }

            val platform = PlatformId.current()
            var copyPaths = this.listBundlePaths()
            if (platform.operatingSystem == OperatingSystem.OSX)
                copyPaths = copyPaths.map { f -> File(f, "${f.name}.app") }

            bundleRepository.download(
                    bundleName,
                    version,
                    platform,
                    destPath = bundleDownloadPath,
                    copyPaths = copyPaths,
                    verify = true,
                    onProgress = { f, p ->
                        if (onProgress != null) onProgress(f, p)
                    })

            Files.move(bundleDownloadPath.toPath(), bundleUpdatePath.toPath())

            readyToInstall = true
        } else {
            log.info("Version [${version}] already downloaded")
            readyToInstall = (readyBundle != null)
        }
        return readyToInstall
    }

    /**
     * Installs previously downloaded bundle-
     * Performs native installation by calling into the bundle process' native entry points
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
        val bundleReadyPath = this.bundleReadyPath(bundleName)

        var bundle: Bundle? = this.tryLoadBundle(bundlePath, bundleName)
        val readyBundle: Bundle? = this.tryLoadBundle(bundleReadyPath, bundleName)

        // Stop and uninstall native bundle process if applicable
        if (!omitNativeInstallation && bundle != null) {
            bundle.stop()
            bundle.uninstall()
        }

        if (bundleReadyPath.exists()) {
            log.info("Updating to [${readyBundle}]")

            val oldBundlePath = this.oldBundlePath(bundleName)
            if (oldBundlePath.exists()) {
                log.info("Removing backup bundle path [${oldBundlePath}]")
                oldBundlePath.deleteRecursively()
            }

            Files.move(bundlePath.toPath(), oldBundlePath.toPath())
            try {
                Files.move(bundleReadyPath.toPath(), bundlePath.toPath())
            } catch(e: Exception) {
                Files.move(oldBundlePath.toPath(), bundlePath.toPath())
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