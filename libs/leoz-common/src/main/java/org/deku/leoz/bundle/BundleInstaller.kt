package org.deku.leoz.bundle

import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import sx.ProcessExecutor
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
        /** Bundle name */
        public val bundleName: String,
        /** Remote bundle repository. The bundle name of this repository has to match the installer name */
        public val repository: BundleRepository) {

    private val log = LogFactory.getLog(this.javaClass)

    companion object {
        private val UPDATE_SUFFIX = ".update"
        private val OLD_SUFFIX = ".old"

        /**
         * Checks if (file/folder) name is a valid module name
         */
        private fun isBundleName(name: String): Boolean {
            return !name.endsWith(UPDATE_SUFFIX) &&
                    !name.endsWith(OLD_SUFFIX)
        }

        /**
         * List bundle container paths. The bundle itself may reside in a subfolder (eg. on OSX it's the .app osx bundle)
         */
        fun listBundlePaths(bundleContainerPath: File): List<File> {
            return bundleContainerPath.listFiles { f -> f.isDirectory && this.isBundleName(f.name) }?.asList() ?: ArrayList()
        }

        /**
         * List bundle names
         */
        fun listBundleNames(bundleContainerPath: File): List<String> {
            return this.listBundlePaths(bundleContainerPath).map { f -> f.name }
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
     * @param name Bundle name
     */
    private fun bundleUpdatePath(): File {
        return File(bundleContainerPath, "${this.bundleName}${UPDATE_SUFFIX}")
    }

    /**
     * Creates bundle path
     */
    private fun bundlePath(): File {
        return File(bundleContainerPath, this.bundleName)
    }

    /**
     * Creates path for old bundle (used when moving into place)
     */
    private fun oldBundlePath(): File {
        return File(bundleContainerPath, "${this.bundleName}${OLD_SUFFIX}")
    }

    /**
     * Current bundle
     */
    public val bundle: Bundle by lazy({
        Bundle(getNativeBundlePath(this.bundlePath()), this.bundleName)
    })

    /**
     * Bundle already exists
     */
    fun hasBundle(): Boolean {
        return this.bundlePath().exists()
    }

    /**
     * Download bundle version
     * @param version Bundle version
     * @param prepareAsUpdate Installs the bundle as an prepared update for an existing bundle
     */
    fun download(version: Bundle.Version,
                 prepareAsUpdate: Boolean,
                 onProgress: ((file: String, percentage: Double) -> Unit)? = null) {

        val destPath = if (prepareAsUpdate) this.bundleUpdatePath() else this.bundlePath()

        val platform = PlatformId.current()
        var comparePaths = this.listBundlePaths().filter { f -> !f.name.equals(this.bundleName) }
        if (platform.operatingSystem == OperatingSystem.OSX)
            comparePaths = comparePaths.map { f -> File(f, "${f.name}.app") }

        repository.download(
                this.bundleName,
                version,
                platform,
                destPath = destPath,
                comparePaths = comparePaths,
                verify = true,
                onProgress = { f, p ->
                    if (onProgress != null) onProgress(f, p)
                })
    }

    /**
     * Applies prepared update. Moves an prepared update into place.
     * @param name Bundle name
     */
    fun applyUpdate() {
        val bundlePath = this.bundlePath()
        val updatePath = this.bundleUpdatePath()

        if (bundlePath.exists()) {
            if (updatePath.exists()) {
                val oldBundlePath = this.oldBundlePath()
                if (oldBundlePath.exists()) {
                    log.info("Removing old bundle path [${oldBundlePath}]")
                    oldBundlePath.deleteRecursively()
                }
                log.info("Moving update into place [${updatePath}] -> [${bundlePath}]")
                bundlePath.renameTo(oldBundlePath)
                updatePath.renameTo(bundlePath)
                oldBundlePath.deleteRecursively()
            }
        } else {
            log.warn("Bundle named [${this.bundleName}] doesn't exist within [${this.bundleContainerPath}]")
        }
    }

    /**
     * List bundle container paths. The bundle itself may reside in a subfolder (eg. on OSX it's the .app osx bundle)
     */
    fun listBundlePaths(): List<File> {
        return listBundlePaths(this.bundleContainerPath)
    }

    /**
     * List bundle names
     */
    fun listBundleNames(): List<String> {
        return listBundleNames(this.bundleContainerPath)
    }
}