package org.deku.leoz.build

import org.apache.commons.logging.LogFactory
import sx.platform.PlatformId
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.util.*

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
        rsyncArtifactUri = rsyncModuleUri.resolve(type.toString())
    }

    /**
     * Rsync client factory helper
     */
    private fun createRsyncClient(): RsyncClient {
        var rc = RsyncClient()
        rc.password = this.rsyncPassword
        rc.compression = 9
        rc.delete = true
        return rc
    }

    /**
     * List artifact versions in remote repository
     */
    public fun list(): List<Artifact.Version> {
        // Get remote list
        var rc = this.createRsyncClient()
        rc.destination =  this.rsyncArtifactUri

        var lr = rc.list()

        // Parse entries to versions
        var result = ArrayList<Artifact.Version>()
        lr.forEach { l ->
            try { if (l.filename != ".") result.add(Artifact.Version.parse(l.filename)) }
            catch(e: Exception) { this.log.warn("Could not parse artifact version [${l.filename}]") }
        }

        return result
    }

    /**
     * Upload artifact version to remote repository
     * @param srcPath Local source path
     * @param version Artifact version this local artifact copy refers to
     * @param syncStartCallback Optional callback providing details about synchronization before start
     * @param fileRecordCallback Optional callback providing details during sync/upload process
     */
    public fun upload(srcPath: File,
                      version: Artifact.Version? = null,
                      syncStartCallback: (src: Rsync.URI?, dst:Rsync.URI?) -> Unit = { s, d -> },
                      fileRecordCallback: (fr: RsyncClient.FileRecord) -> Unit = { } ) {
        if (version == null) {
            // TODO: read version from manifest
            throw IllegalArgumentException("Version is mandatory")
        }

        // TODO: verify this is an artifact version folder (having only platform ids as subfolder)

        // Take the two most recent versions for comparison during sync
        var comparisonDestinationUris = this.list()
                .sortDescending()
                .filter( { v -> v.compareTo(version) != 0 } )
                .take(2)
                .map( { v -> URI("../").resolve(v.toString()) } )

        var rc = this.createRsyncClient()
        rc.source = Rsync.URI(srcPath)
        rc.destination = this.rsyncArtifactUri.resolve(version.toString())
        rc.copyDestinations = comparisonDestinationUris

        log.info("Synchronizing [${rc.source}] -> [${rc.destination}]")
        syncStartCallback(rc.source, rc.destination)

        rc.sync( { r ->
            log.info("Uploading ${r.path}")
            fileRecordCallback(r)
        } )
    }

    /**
     * Download artifact version from remote repository
     * @param destPath Local destination path
     */
    public fun download(version: Artifact.Version, platformId: PlatformId, destPath: File) {
        var rc = this.createRsyncClient()
        rc.source = this.rsyncArtifactUri.resolve(version.toString()).resolve(platformId.toString())
        rc.destination = Rsync.URI(destPath)

        log.info("Downloading [${rc.source}] -> [${rc.destination}]")
        rc.sync( { r -> log.info("Downloading ${r.path}") } )

        log.info("Verifying artifact")
        Artifact.load(destPath)
                .verify()
    }
}