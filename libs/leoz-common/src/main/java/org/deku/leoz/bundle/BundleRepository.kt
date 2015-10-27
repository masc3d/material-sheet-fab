package org.deku.leoz.bundle

import org.apache.commons.logging.LogFactory
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.function.BiPredicate

/**
 * Provices access to a remote rsync bundle repository, download and upload operations
 * @param rsyncModuleUri The base rsync module uri. The expected directory structur is $artifact-name/$version/$platform
 * Created by masc on 24.08.15.
 */
class BundleRepository(val rsyncModuleUri: Rsync.URI, val rsyncPassword: String) {
    val log = LogFactory.getLog(this.javaClass)

    /**
     * Rsync client factory helper
     */
    private fun createRsyncClient(): RsyncClient {
        val rc = RsyncClient()
        rc.password = this.rsyncPassword
        rc.compression = 9
        rc.delete = true
        return rc
    }

    /**
     * List  artifact versions in remote repository
     */
    fun listVersions(bundleName: String): List<Bundle.Version> {
        // Get remote list
        val rc = this.createRsyncClient()
        rc.destination = this.rsyncModuleUri.resolve(bundleName)

        val lr = rc.list()

        // Parse entries to versions
        val result = ArrayList<Bundle.Version>()
        lr.forEach { l ->
            try {
                if (l.filename != ".") result.add(Bundle.Version.parse(l.filename))
            } catch(e: Exception) {
                this.log.warn("Could not parse artifact version [${l.filename}]")
            }
        }

        return result
    }

    /**
     * List remote platform ids of a specific artifact version in remote repository
     * @param version Bundle version
     */
    fun listPlatforms(bundleName: String, version: Bundle.Version): List<PlatformId> {
        val rc = this.createRsyncClient()
        rc.destination = this.rsyncModuleUri.resolve(bundleName).resolve(version)

        return rc.list().asSequence()
                .filter { l -> !l.filename.startsWith(".") }
                .map { l -> PlatformId.parse(l.filename) }
                .toArrayList()
    }

    /**
     * Walk platform folders lazily
     * @return Stream of platform paths
     */
    private fun walkPlatformFolders(path: Path): java.util.stream.Stream<Path> {
        return Files.find(path,
                1,
                BiPredicate { p, b -> !p.equals(path) && !p.fileName.toString().startsWith(".") })
    }

    /**
     * Bundle path for specific platform
     * @param path Base path
     * @param platform Platform
     */
    private fun bundlePath(bundleName: String, path: File, platform: PlatformId): File {
        return if (platform.operatingSystem == OperatingSystem.OSX) File(path, "${bundleName}.app") else path
    }

    /**
     * Bundle path
     */
    private fun bundlePath(bundleName: String, path: File): File {
        return this.bundlePath(bundleName, path, PlatformId.current())
    }

    /**
     * Upload all platforms of a specific bundle version to remote repository
     * @param bundleName Name of bundle to upload
     * @param versionSrcPath Local version source path. The folder is expected to have platform id subdirs
     * @param consoleOutput Log to console instead of logger (used for gradle)
     */
    @JvmOverloads fun upload(bundleName: String,
                             versionSrcPath: File,
                             consoleOutput: Boolean = false) {
        /** Info logging wrapper */
        fun logInfo(s: String) {
            if (consoleOutput) println(s) else log.info(s)
        }

        logInfo("Upload sequence start")

        val nSrcPath = Paths.get(versionSrcPath.toURI())

        // Verify this is an artifact version folder (having only platform ids as subfolder)
        val bundles = ArrayList<Bundle>()
        this.walkPlatformFolders(nSrcPath).forEach { p ->
            val bundlePath = this.bundlePath(bundleName, p.toFile(), PlatformId.parse(p.fileName.toString()))
            val a = Bundle.load(bundlePath)
            logInfo("Found [${a}]")
            bundles.add(a)
        }

        if (bundles.size > 1) {
            bundles.takeLast(bundles.size - 1).forEach { a ->
                if (!bundles[0].version!!.equals(a.version))
                    throw IllegalStateException("Inconsistent artifact versions")
                if (!bundles[0].javaVersion.equals(a.javaVersion))
                    throw IllegalStateException("Inconsistent java versions")
            }
        }

        if (bundles.isEmpty())
            throw IllegalStateException("No artifacts found in path")

        val version = bundles.get(0).version!!
        val remoteVersions = this.listVersions(bundleName)

        val comparisonDestinationVersions = remoteVersions.sortedDescending()
                .filter({ v -> v.compareTo(version) != 0 })
                .take(2)

        if (!remoteVersions.contains(version)) {
            logInfo("Version does not exist remotely, transferring all platforms")
            // Transfer entire version folder
            // Take the two most recent versions for comparison during sync
            val comparisonDestinationUris = comparisonDestinationVersions
                    .map({ v -> Rsync.URI("../").resolve(v) })

            val rc = this.createRsyncClient()
            rc.source = Rsync.URI(versionSrcPath)
            rc.destination = this.rsyncModuleUri.resolve(bundleName).resolve(version)
            rc.copyDestinations = comparisonDestinationUris

            logInfo("Synchronizing [${rc.source}] -> [${rc.destination}]")

            rc.sync({ r ->
                logInfo("Uploading ${r.path}")
            })
        } else {
            logInfo("Version already exists remotely")

            // Transfer artifacts separately, to prevent non-existing platforms to be deleted remotely
            for (bundle in bundles) {
                // Take the two most recent versions for comparison during sync
                val comparisonDestinationUris = comparisonDestinationVersions
                        .map({ v -> Rsync.URI("../../").resolve(v, bundle.platform!!) })

                val rc = this.createRsyncClient()
                rc.source = Rsync.URI(versionSrcPath).resolve(bundle.platform!!)
                rc.destination = this.rsyncModuleUri.resolve(bundleName).resolve(bundle.version!!, bundle.platform!!)
                rc.copyDestinations = comparisonDestinationUris

                logInfo("Synchronizing [${rc.source}] -> [${rc.destination}]")

                rc.sync({ r ->
                    logInfo("Updating [${r.flags}] [${r.path}]")
                })
            }
        }

        logInfo("Upload sequence complete")
    }

    /**
     * Download a specific platform/version of a bundle from remote repository
     * @param bundleName Name of bundle to download
     * @param version Bundle version
     * @param platformId Platform id
     * @param destPath Local destination path
     * @param copyPaths (Optional) Comparison/copy paths (for rsync to cross reference, minimizing download volume)
     * @param verify Verify bundle after successful download. Defaults to false.
     * @param onProgress (Optional) Progress callback
     */
    @JvmOverloads fun download(bundleName: String,
                               version: Bundle.Version,
                               platformId: PlatformId,
                               destPath: File,
                               copyPaths: List<File> = ArrayList(),
                               verify: Boolean = false,
                               onProgress: ((file: String, percentage: Double) -> Unit)? = null) {
        val rc = this.createRsyncClient()

        val isOsx = platformId.operatingSystem == OperatingSystem.OSX

        var source = this.rsyncModuleUri.resolve(bundleName).resolve(version, platformId)
        var destination = Rsync.URI(destPath)
        var copyDestinations = copyPaths.asSequence().map { Rsync.URI(it) }

        if (isOsx) {
            val osxBundleName = "${bundleName}.app"
            source = source.resolve(osxBundleName)
            destination = destination.resolve(osxBundleName)
            if (destPath.parentFile.exists())
                File(destPath, osxBundleName).mkdirs()
        }

        rc.source = source
        rc.destination = destination
        rc.preservePermissions = false
        rc.preserveExecutability = true
        rc.preserveGroup = false
        rc.preserveOwner = false
        rc.copyDestinations = copyDestinations.toArrayList()

        log.info("Synchronizing [${rc.source}] -> [${rc.destination}]")

        var currentFile: String = ""
        var currentPercentage: Double = 0.0
        if (onProgress != null) onProgress(currentFile, 0.0)
        rc.sync(
                onFile = { r ->
                    log.info("Updating [${r.flags}] [${r.path}]")
                    currentFile = r.path
                    if (onProgress != null) onProgress(currentFile, currentPercentage)
                },
                onProgress = { p ->
                    currentPercentage = p.percentage.toDouble() / 100
                    if (onProgress != null) onProgress(currentFile, currentPercentage)
                })

        if (onProgress != null) onProgress(currentFile, 0.95)

        if (verify) {
            val bundlePath = this.bundlePath(bundleName, destPath)
            log.info("Verifying bundle [${bundlePath}]")
            Bundle.load(bundlePath)
                    .verify()
        }

        if (onProgress != null) onProgress(currentFile, 1.0)
    }

    /**
     * Download all platform bundles of a specific version from remote repository
     * @param bundleName Name of bundle to download
     * @param version Bundle version
     * @param destPath Destination path
     * @param verify Verify bundle after download
     * @param consoleOutput Log to console instead of logger (used for gradle)
     */
    @JvmOverloads fun download(bundleName: String,
                               version: Bundle.Version,
                               destPath: File,
                               verify: Boolean = false,
                               consoleOutput: Boolean = false) {
        fun logInfo(s: String) {
            if (consoleOutput) println(s) else log.info(s)
        }

        val rc = this.createRsyncClient()
        rc.source = this.rsyncModuleUri.resolve(bundleName).resolve(version)
        rc.destination = Rsync.URI(destPath)
        rc.preservePermissions = false
        rc.preserveExecutability = true
        rc.preserveGroup = false
        rc.preserveOwner = false

        logInfo("Synchronizing [${rc.source}] -> [${rc.destination}]")
        rc.sync({ r ->
            logInfo("Updating [${r.flags}] [${r.path}]")
        })

        if (verify) {
            this.walkPlatformFolders(destPath.toPath()).forEach { p ->
                val bundlePath = this.bundlePath(bundleName, p.toFile(), PlatformId.parse(p.fileName.toString()))
                logInfo("Verifying bundle [${bundlePath}]")
                Bundle.load(bundlePath).verify()
            }
        }

        logInfo("Download sequence complete")
    }
}