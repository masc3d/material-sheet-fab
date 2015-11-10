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
 * @param rsyncPassword Rsync password
 * Created by masc on 24.08.15.
 */
class BundleRepository(
        val rsyncModuleUri: Rsync.URI,
        val rsyncPassword: String = "")
{
    val log = LogFactory.getLog(this.javaClass)

    companion object {
        val DOWNLOAD_SUFFIX = ".download"
    }

    /**
     * Rsync client factory helper
     */
    private fun createRsyncClient(): RsyncClient {
        val rc = RsyncClient()
        rc.password = this.rsyncPassword
        rc.compression = 9
        rc.delete = true
        rc.preservePermissions = false
        rc.preserveExecutability = true
        rc.preserveGroup = false
        rc.preserveOwner = false
        return rc
    }

    private fun isValidFilename(filename: String): Boolean {
        return !filename.startsWith(".") && !filename.endsWith(DOWNLOAD_SUFFIX)
    }

    /**
     * Walk platform folders lazily
     * @return Stream of platform paths
     */
    private fun walkPlatformFolders(path: Path): java.util.stream.Stream<Path> {
        return Files.find(path,
                1,
                BiPredicate { p, b ->
                    !p.equals(path) && this.isValidFilename(p.fileName.toString())
                })
    }

    /**
     * Bundle path for specific platform
     * @param path Base path
     * @param bundleName Bundle name
     * @param platform Platform
     */
    private fun bundlePath(path: File, bundleName: String, platform: PlatformId): File {
        return if (platform.operatingSystem == OperatingSystem.OSX) File(path, "${bundleName}.app") else path
    }

    /**
     * Bundle path
     * @param path Base path
     * @param bundleName Bundle name
     */
    private fun bundlePath(path: File, bundleName: String): File {
        return this.bundlePath(path, bundleName, PlatformId.current())
    }

    /**
     * List filenames
     * @param rsyncUri Rsync URI
     * @return List of filenames
     */
    private fun list(rsyncUri: Rsync.URI): List<String> {
        // Get remote list
        val entries: List<String>
        if (rsyncUri.isFile()) {
            entries = File(rsyncUri.uri).listFiles().map { l -> l.name }
        } else {
            val rc = this.createRsyncClient()
            rc.destination = rsyncUri
            entries = rc.list().map { l -> l.filename }
        }
        return entries
    }

    /**
     * List bundles within repository
     */
    fun listBundles(): List<String> {
        return this.list(this.rsyncModuleUri).filter { f -> this.isValidFilename(f) }
    }

    /**
     * List bundle versions in repository
     * @param bundleName Bundle name
     */
    fun listVersions(bundleName: String): List<Bundle.Version> {
        val filenames = this.list(
                this.rsyncModuleUri.resolve(bundleName))

        // Parse entries to versions
        val result = ArrayList<Bundle.Version>()
        filenames.forEach { entry ->
            try {
                if (this.isValidFilename(entry))
                    result.add(Bundle.Version.parse(entry))
            } catch(e: Exception) {
                this.log.warn("Could not parse artifact version [${entry}]")
            }
        }

        return result
    }

    /**
     * List remote platform ids of a specific artifact version in repository
     * @param version Bundle version
     */
    fun listPlatforms(bundleName: String, version: Bundle.Version): List<PlatformId> {
        val filenames = this.list(
                this.rsyncModuleUri.resolve(bundleName).resolve(version))

        return filenames
                .filter { f -> this.isValidFilename(f) }
                .map { f -> PlatformId.parse(f) }
                .toArrayList()
    }

    /**
     * Query for latest version matching a version pattern
     * @param bundleName Bundle name
     * @param versionPattern Version pattern
     * @throws NoSuchElementException If version was not found within repository
     */
    fun queryLatestMatchingVersion(bundleName: String, versionPattern: String): Bundle.Version {
        // Determine remote version matching version pattern
        return this
                .listVersions(bundleName)
                .filter(versionPattern)
                .sortedDescending()
                .firstOrNull() ?: throw NoSuchElementException("Repository [${this}] has no bundle [${bundleName}] with version matching [${versionPattern}]")
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
        val logInfo: (s: String) -> Unit
        if (consoleOutput)
            logInfo = { s -> println(s) }
        else
            logInfo = { s -> this.log.info(s) }

        logInfo("Upload sequence start")

        val nSrcPath = Paths.get(versionSrcPath.toURI())

        // Verify this is an artifact version folder (having only platform ids as subfolder)
        val bundles = ArrayList<Bundle>()
        this.walkPlatformFolders(nSrcPath).forEach { p ->
            val bundlePath = this.bundlePath(p.toFile(), bundleName, PlatformId.parse(p.fileName.toString()))
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

        val copyDestinationVersions = remoteVersions.sortedDescending()
                .filter({ v -> v.compareTo(version) != 0 })
                .take(2)

        if (!remoteVersions.contains(version)) {
            logInfo("Version does not exist remotely, transferring all platforms")
            // Transfer entire version folder
            // Take the two most recent versions for comparison during sync
            val copyDestinationUris = copyDestinationVersions
                    .map({ v -> Rsync.URI("../").resolve(v) })

            val rc = this.createRsyncClient()
            rc.source = Rsync.URI(versionSrcPath)
            rc.destination = this.rsyncModuleUri.resolve(bundleName).resolve(version)
            rc.copyDestinations = copyDestinationUris

            logInfo("Synchronizing [${rc.source}] -> [${rc.destination}]")

            rc.sync({ r ->
                logInfo("Uploading ${r.path}")
            })
        } else {
            logInfo("Version already exists remotely")

            // Transfer artifacts separately, to prevent non-existing platforms to be deleted remotely
            for (bundle in bundles) {
                // Take the two most recent versions for comparison during sync (per platform)
                val copyDestinationUris = copyDestinationVersions
                        .map({ v -> Rsync.URI("../../").resolve(v, bundle.platform!!) })

                val rc = this.createRsyncClient()
                rc.source = Rsync.URI(versionSrcPath).resolve(bundle.platform!!)
                rc.destination = this.rsyncModuleUri.resolve(bundleName).resolve(bundle.version!!, bundle.platform!!)
                rc.copyDestinations = copyDestinationUris

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
            val bundlePath = this.bundlePath(destPath, bundleName)
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
     * @param copyPaths (Optional) Comparison/copy paths (for rsync to cross reference, minimizing download volume)
     * @param verify Verify bundle after download
     * @param consoleOutput Log to console instead of logger (used for gradle)
     */
    @JvmOverloads fun download(bundleName: String,
                               version: Bundle.Version,
                               destPath: File,
                               copyPaths: List<File> = ArrayList(),
                               verify: Boolean = false,
                               consoleOutput: Boolean = false) {
        fun logInfo(s: String) {
            if (consoleOutput) println(s) else log.info(s)
        }

        val rc = this.createRsyncClient()
        rc.source = this.rsyncModuleUri.resolve(bundleName).resolve(version)
        rc.destination = Rsync.URI(destPath)
        rc.copyDestinations = copyPaths.map { f -> Rsync.URI(f) }

        logInfo("Synchronizing [${rc.source}] -> [${rc.destination}]")
        rc.sync({ r ->
            logInfo("Updating [${r.flags}] [${r.path}]")
        })

        if (verify) {
            this.walkPlatformFolders(destPath.toPath()).forEach { p ->
                val bundlePath = this.bundlePath(p.toFile(), bundleName, PlatformId.parse(p.fileName.toString()))
                logInfo("Verifying bundle [${bundlePath}]")
                Bundle.load(bundlePath).verify()
            }
        }

        logInfo("Download sequence complete")
    }

    /**
     * Download all platform bundles of a specific version to a local repository.
     * The bundle being downloaded will have a download suffix until it's successfully verified
     * @param bundleName Name of bundle to download
     * @param version Bundle version
     * @param localRepository Local repository. The URI of this repository must be local, otherwise an exception is thrown
     */
    fun download(bundleName: String,
                 version: Bundle.Version,
                 localRepository: BundleRepository) {

        if (!localRepository.rsyncModuleUri.isFile())
            throw IllegalStateException("Local repository url is supposed to be a file [${rsyncModuleUri}]")

        val localVersions = localRepository.listVersions(bundleName)

        val copyDestinationUris = localVersions.sortedDescending()
                .filter({ v -> v.compareTo(version) != 0 })
                .take(2)
                .map({ v -> Rsync.URI("../").resolve(v) })

        val rc = this.createRsyncClient()
        rc.source = this.rsyncModuleUri.resolve(bundleName).resolve(version)
        rc.destination = localRepository.rsyncModuleUri.resolve(bundleName).resolve(version.toString() + DOWNLOAD_SUFFIX)
        rc.copyDestinations = copyDestinationUris

        log.info("Synchronizing [${rc.source}] -> [${rc.destination}]")
        rc.sync({ r ->
            log.info("Updating [${r.flags}] [${r.path}]")
        })
    }
}