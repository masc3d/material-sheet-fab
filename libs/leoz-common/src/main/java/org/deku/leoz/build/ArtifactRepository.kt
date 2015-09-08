package org.deku.leoz.build

import org.apache.commons.logging.LogFactory
import sx.platform.PlatformId
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util
import java.util.*
import java.util.function.BiPredicate
import java.util.function.IntSupplier

/**
 * Provices access to a remote rsync artifact repository
 *
 * @param rsyncModuleUri The base rsync module uri. The expected directory structur is $artifact-name/$version/$platform
 * Created by masc on 24.08.15.
 */
public class ArtifactRepository(val type: Artifact.Type, val rsyncModuleUri: Rsync.URI, val rsyncPassword: String) {
    val log = LogFactory.getLog(this.javaClass)

    val rsyncArtifactUri: Rsync.URI

    init {
        rsyncArtifactUri = rsyncModuleUri.resolve(type)
    }

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
    public fun listVersions(): List<Artifact.Version> {
        // Get remote list
        val rc = this.createRsyncClient()
        rc.destination = this.rsyncArtifactUri

        val lr = rc.list()

        // Parse entries to versions
        val result = ArrayList<Artifact.Version>()
        lr.forEach { l ->
            try {
                if (l.filename != ".") result.add(Artifact.Version.parse(l.filename))
            } catch(e: Exception) {
                this.log.warn("Could not parse artifact version [${l.filename}]")
            }
        }

        return result
    }

    /**
     * List remote platform ids of a specific artifact version in remote repository
     * @param version Artifact version
     */
    public fun listPlatforms(version: Artifact.Version): List<PlatformId> {
        val rc = this.createRsyncClient()
        rc.destination = this.rsyncArtifactUri.resolve(version)

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
                BiPredicate { p, b -> !p.equals(path) && !p.getFileName().toString().startsWith(".") })
    }

    /**
     * Upload artifact version to remote repository
     * @param srcPath Local source path
     * @param syncStartCallback Optional callback providing details about synchronization before start
     * @param fileRecordCallback Optional callback providing details during sync/upload process
     */
    public @jvmOverloads fun upload(srcPath: File,
                                    syncStartCallback: (src: Rsync.URI?, dst: Rsync.URI?) -> Unit = { s, d -> },
                                    fileRecordCallback: (fr: RsyncClient.FileRecord) -> Unit = { }) {
        val nSrcPath = Paths.get(srcPath.toURI())

        // Verify this is an artifact version folder (having only platform ids as subfolder)
        val artifacts = ArrayList<Artifact>()
        this.walkPlatformFolders(nSrcPath).forEach { p ->
            artifacts.add(Artifact.load(p.toFile()))
        }

        if (artifacts.isEmpty())
            throw IllegalStateException("No artifacts found in path")

        val version = artifacts.get(0).version!!
        val remoteVersions = this.listVersions()

        if (remoteVersions.contains(version))
            throw IllegalArgumentException("Version [${version}] already exists remotely")

        // Take the two most recent versions for comparison during sync
        val comparisonDestinationUris = remoteVersions.sortDescending()
                .filter({ v -> v.compareTo(version) != 0 })
                .take(2)
                .map({ v -> Rsync.URI("../").resolve(v) })

        val rc = this.createRsyncClient()
        rc.source = Rsync.URI(srcPath)
        rc.destination = this.rsyncArtifactUri.resolve(version)
        rc.copyDestinations = comparisonDestinationUris

        log.info("Synchronizing [${rc.source}] -> [${rc.destination}]")
        syncStartCallback(rc.source, rc.destination)

        rc.sync({ r ->
            log.info("Uploading ${r.path}")
            fileRecordCallback(r)
        })
    }

    /**
     * Upload artifact version/platform to remote repository
     * @param srcPath Local source path
     * @param platform Platform id
     * @param syncStartCallback Optional callback providing details about synchronization before start
     * @param fileRecordCallback Optional callback providing details during sync/upload process
     */
    public fun upload(srcPath: File,
                      platform: PlatformId,
                      syncStartCallback: (src: Rsync.URI?, dst: Rsync.URI?) -> Unit = { s, d -> },
                      fileRecordCallback: (fr: RsyncClient.FileRecord) -> Unit = { }) {
        val artifact = Artifact.load(srcPath)

        val remoteVersions = this.listVersions()
        if (remoteVersions.contains(artifact.version)) {
            if (this.listPlatforms(artifact.version!!).contains(platform))
                throw IllegalStateException("Artifact [${artifact}] already exists remotely")
        }

        // Take the two most recent versions for comparison during sync
        val comparisonDestinationUris = remoteVersions.sortDescending()
                .filter({ v -> v.compareTo(artifact.version!!) != 0 })
                .take(2)
                .map({ v -> Rsync.URI("../../").resolve(v, platform) })

        val rc = this.createRsyncClient()
        rc.source = Rsync.URI(srcPath)
        rc.destination = this.rsyncArtifactUri.resolve(artifact.version!!, platform)
        rc.copyDestinations = comparisonDestinationUris

        log.info("Synchronizing [${rc.source}] -> [${rc.destination}]")
        syncStartCallback(rc.source, rc.destination)

        rc.sync({ r ->
            log.info("Uploading ${r.path}")
            fileRecordCallback(r)
        })
    }

    /**
     * Download a specific platform/version of an artifact from remote repository
     * @param destPath Local destination path
     * @param version Artifact version
     * @param platformId Platform id
     */
    public @jvmOverloads fun download(version: Artifact.Version, platformId: PlatformId, destPath: File, verify: Boolean = false) {
        val rc = this.createRsyncClient()
        rc.source = this.rsyncArtifactUri.resolve(version, platformId)
        rc.destination = Rsync.URI(destPath)

        log.info("Downloading [${rc.source}] -> [${rc.destination}]")
        rc.sync({ r -> log.info("Downloading ${r.path}") })

        if (verify) {
            log.info("Verifying artifact")
            Artifact.load(destPath)
                    .verify()
        }
    }

    /**
     * Download a specific version of an artifact from remote repository (all platforms)
     * @param version Artifact version
     * @üaram destPath Destination path
     */
    public @jvmOverloads fun download(version: Artifact.Version, destPath: File, verify: Boolean = false) {
        val rc = this.createRsyncClient()
        rc.source = this.rsyncArtifactUri.resolve(version)
        rc.destination = Rsync.URI(destPath)

        log.info("Downloading [${rc.source}] -> [${rc.destination}]")
        rc.sync({ r -> log.info("Downloading ${r.path}") })

        if (verify) {
            this.walkPlatformFolders(destPath.toPath()).forEach { p ->
                Artifact.load(p.toFile()).verify()
            }
        }
    }
}