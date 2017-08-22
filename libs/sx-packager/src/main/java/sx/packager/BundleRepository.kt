package sx.packager

import org.slf4j.LoggerFactory
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import sx.ssh.SshTunnelProvider
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.function.BiPredicate
import java.util.stream.Stream

/**
 * Provices access to a remote rsync bundle repository, download and upload operations
 * @property rsyncModuleUri The base rsync module uri. The expected directory structur is $artifact-name/$version/$platform
 * @property rsyncPassword Rsync password
 * @property rsyncSshTunnel Rsync SSH tunnel
 * Created by masc on 24.08.15.
 */
class BundleRepository(
        val rsyncModuleUri: Rsync.URI,
        val rsyncPassword: String = "",
        val sshTunnelProvider: SshTunnelProvider? = null)
{
    val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val DOWNLOAD_SUFFIX = ".download"

        /**
         * Checks if repository is local and throws exception if not
         * @param repository Bundle repository to check
         * @throws IllegalArgumentException If repository is not local
         */
        fun assertLocalRepository(repository: BundleRepository) {
            if (!repository.rsyncModuleUri.isFile())
                throw IllegalStateException("Repository [${repository}] is supposed to be local, but has remote url")
        }
    }

    /**
     * Rsync client factory helper
     */
    private fun createRsyncClient(): RsyncClient {
        val rc = RsyncClient()
        rc.password = this.rsyncPassword
        rc.compression = 2
        rc.delete = true
        rc.preservePermissions = false
        rc.preserveExecutability = true
        rc.preserveGroup = false
        rc.preserveOwner = false
        rc.sshTunnelProvider = this.sshTunnelProvider
        return rc
    }

    private fun isValidFilename(filename: String): Boolean {
        return !filename.startsWith(".") && !filename.endsWith(DOWNLOAD_SUFFIX)
    }

    /**
     * Walk platform folders lazily
     * @return Stream of platform paths
     */
    private fun walkPlatformFolders(path: Path): Stream<Path> {
        return Files.find(path,
                1,
                BiPredicate { p, _ ->
                    p != path && this.isValidFilename(p.fileName.toString())
                })
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
            entries = (File(rsyncUri.uri).listFiles() ?: arrayOf<File>())
                    .map { l -> l.name }
        } else {
            val rc = this.createRsyncClient()
            entries = rc.list(rsyncUri).map { l -> l.filename }
        }
        return entries
    }

    /**
     * List bundles within repository
     */
    fun listBundles(): List<String> {
        if (!this.rsyncModuleUri.isFile())
            log.info("Retrieving list of bundles from [${this.rsyncModuleUri}]")

        return this.list(this.rsyncModuleUri).filter { f -> this.isValidFilename(f) }
    }

    /**
     * List bundle versions in repository
     * @param bundleName Bundle name
     */
    fun listVersions(bundleName: String): List<Bundle.Version> {
        if (!this.rsyncModuleUri.isFile())
            log.info("Retrieving versions of bundle [${bundleName}] from [${this.rsyncModuleUri}]")

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
                .toMutableList()
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
            val bundlePath = p.toFile()
            val a = Bundle.load(bundlePath)
            logInfo("Found [${a}]")
            bundles.add(a)
        }

        if (bundles.size > 1) {
            bundles.takeLast(bundles.size - 1).forEach { a ->
                if (bundles[0].version!! != a.version)
                    throw IllegalStateException("Inconsistent artifact versions")
                if (bundles[0].javaVersion != a.javaVersion)
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

        /**
         * Internal method performing sync
         */
        fun sync(source: Rsync.URI, destination: Rsync.URI, copyDestinations: List<Rsync.URI>) {
            logInfo("Synchronizing [${source}] -> [${destination}] using copy destinations [${copyDestinations.joinToString(", ")}[")

            val rc = this.createRsyncClient()
            rc.copyDestinations = copyDestinations
            rc.sync(source, destination, { r ->
                logInfo("Updating [${r.flags}] [${r.path}]")
            })
        }

        if (!remoteVersions.contains(version)) {
            logInfo("Version does not exist remotely, transferring all platforms")
            // Transfer entire version folder
            // Take the two most recent versions for comparison during sync
            val copyDestinationUris = copyDestinationVersions
                    .map({ v -> Rsync.URI("../").resolve(v) })

            val source = Rsync.URI(versionSrcPath)
            val destination = this.rsyncModuleUri.resolve(bundleName).resolve(version)

            sync(source, destination, copyDestinationUris)
        } else {
            logInfo("Version already exists remotely")

            // Transfer artifacts separately, to prevent non-existing platforms to be deleted remotely
            for (bundle in bundles) {
                // Take the two most recent versions for comparison during sync (per platform)
                val copyDestinationUris = copyDestinationVersions
                        .map({ v -> Rsync.URI("../../").resolve(v, bundle.platform!!) })

                val source = Rsync.URI(versionSrcPath).resolve(bundle.platform!!)
                val destination = this.rsyncModuleUri.resolve(bundleName).resolve(bundle.version!!, bundle.platform!!)

                sync(source, destination, copyDestinationUris)
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

        val source: Rsync.URI
        val destination: Rsync.URI
        val copyDestinations: List<Rsync.URI>

        when (platformId.operatingSystem) {
            OperatingSystem.OSX -> {
                // OSX specific optimization for copying from other local bundles.
                // Amending source/dest/copy paths to consistently point inside the OSX bundle specific app folder
                val osxBundleName = "${bundleName}.app"
                source = this.rsyncModuleUri
                        .resolve(bundleName)
                        .resolve(version, platformId)
                        .resolve(osxBundleName)
                destination = Rsync.URI(destPath)
                        .resolve(osxBundleName)
                copyDestinations = copyPaths.map { Rsync.URI(it).resolve("${it.name}.app") }
                File(destPath, osxBundleName).mkdirs()
            }
            else -> {
                source = this.rsyncModuleUri
                        .resolve(bundleName)
                        .resolve(version, platformId)
                destination = Rsync.URI(destPath)
                copyDestinations = copyPaths.map { Rsync.URI(it) }
            }
        }

        log.info("Synchronizing [${source}] -> [${destination}]")

        var currentFile: String = ""
        var currentPercentage: Double = 0.0
        if (onProgress != null) onProgress(currentFile, 0.0)
        rc.copyDestinations = copyDestinations
        rc.sync(source, destination,
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
            val bundlePath = destPath
            log.info("Verifying bundle [${bundlePath}]")
            Bundle.load(bundlePath).verify()
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
        val source = this.rsyncModuleUri.resolve(bundleName).resolve(version)
        val destination = Rsync.URI(destPath)
        rc.copyDestinations = copyPaths.map { f -> Rsync.URI(f) }

        logInfo("Synchronizing [${source}] -> [${destination}]")
        rc.sync(source, destination, { r ->
            logInfo("Updating [${r.flags}] [${r.path}]")
        })

        if (verify) {
            this.walkPlatformFolders(destPath.toPath()).forEach { p ->
                val bundlePath = p.toFile()
                logInfo("Verifying bundle [${bundlePath}]")
                Bundle.load(bundlePath).verify()
            }
        }

        logInfo("Download sequence complete")
    }

    /**
     * Download all platform bundles of a specific version to a local repository.
     * The bundle being downloaded will have a download suffix until it's successfully verified.
     * @param bundleName Name of bundle to download
     * @param version Bundle version
     * @param localRepository Local repository. The URI of this repository must be local, otherwise an exception is thrown
     * @return true if version was downloaded and verified successfully. false if it already exists
     * @throws IllegalStateException If repository (url) is not local
     */
    fun download(bundleName: String,
                 version: Bundle.Version,
                 localRepository: BundleRepository): Boolean {

        assertLocalRepository(localRepository)

        // Local repository basepath
        val localRepoBasePath = File(localRepository.rsyncModuleUri.uri)
        val localRepoBundlePath = localRepoBasePath.resolve(bundleName)

        // Final destination path for download
        val destPath = localRepoBundlePath
                .resolve(version.toString())

        // Temporary download path
        val downloadPath = destPath.parentFile.resolve(destPath.name + DOWNLOAD_SUFFIX)

        if (destPath.exists()) {
            Files.move(destPath.toPath(), downloadPath.toPath())
        } else {
            downloadPath.mkdirs()
        }

        // Local version of bundle
        val localVersions = localRepository.listVersions(bundleName)

        // TODO. also include paths of other bundles. a good way to do it would possibly
        // to use the latest of any bundle to minimize transfer volume.
        // This should be done on a repo/bundle rsync URI basis, so the logic can be reused
        // for remote URIs (eg. upload) as well
        val copyDestinationPaths = localVersions.sortedDescending()
                .filter({ v -> v.compareTo(version) != 0 })
                .take(2)
                .map({ v -> localRepoBundlePath.resolve(v.toString()) })

        this.download(bundleName = bundleName,
                version = version,
                destPath = downloadPath,
                copyPaths = copyDestinationPaths,
                verify = true)

        Files.move(downloadPath.toPath(), destPath.toPath())
        return true
    }

    /**
     * Clean repository while keeping preserved bundles.
     * This method is only supported for local repositories
     * @param bundlesToPreserve List of bundle (names) to preserve
     */
    fun cleanBundles(bundlesToPreserve: List<String>) {
        assertLocalRepository(this)

        log.info("Cleaning bundles within [${this}] preserving [${bundlesToPreserve.joinToString(", ")}]")

        val basePath = File(this.rsyncModuleUri.uri)

        this.listBundles()
                .filter { bundleName -> !bundlesToPreserve.contains(bundleName) }
                .forEach { bundleName ->
                    val bundlePath = basePath.resolve(bundleName)
                    log.info("Deleting [${bundlePath}]")
                    bundlePath.deleteRecursively()
                }
    }

    /**
     * Clean specific bundle within repository, keeping preserved versions
     * @param bundleName Bundle (name) to process
     * @param versionsToPreserve Bundle versions to preserve
     */
    fun cleanVersions(bundleName: String, versionsToPreserve: List<Bundle.Version>) {
        assertLocalRepository(this)

        log.info("Cleaning versions for bundle [${bundleName}] within [${this}] preserving [${versionsToPreserve.joinToString(", ")}]")

        val basePath = File(this.rsyncModuleUri.uri)

        this.listVersions(bundleName).filter { version -> !versionsToPreserve.contains(version) }
                .forEach { version ->
                    val bundleVersionPath = basePath.resolve(bundleName).resolve(version.toString())
                    log.info("Deleting [${bundleVersionPath}]")
                    bundleVersionPath.deleteRecursively()
                }

        log.info("Completed cleanup for bundle [${bundleName}]")
    }

    /**
     * @property name Bundle name
     * @property pattenr Bundle version pattern
     */
    data class PreserveSpec(val name: String, val pattern: String)

    /**
     * Clean versions
     * @param preserve Preservation spec
     */
    fun cleanVersions(preserve: List<PreserveSpec>) {
        val names = preserve.map { it.name }.distinct()

        for (name in names) {
            val patternsToPreserve = preserve.filter { it.name == name }.map { it.pattern }

            val versions = this.listVersions(name)

            val versionsToPreserve = patternsToPreserve.mapNotNull {
                versions.filter(pattern = it)
                        .sortedDescending()
                        .firstOrNull()
            }.distinct()

            this.cleanVersions(bundleName = name, versionsToPreserve = versionsToPreserve)
        }
    }

    override fun toString(): String {
        return this.rsyncModuleUri.toString()
    }
}