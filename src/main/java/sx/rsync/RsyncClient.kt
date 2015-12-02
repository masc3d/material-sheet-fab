package sx.rsync

import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import sx.ProcessExecutor
import sx.io.PermissionUtil
import sx.ssh.SshTunnelProvider
import java.io.File
import java.net.URI
import java.time.LocalDateTime
import java.util.*
import kotlin.text.Regex

/**
 * Created by masc on 15.08.15.
 */
class RsyncClient() {
    companion object {
        val log = LogFactory.getLog(RsyncClient::class.java)
    }

    /** Remote source/destination password */
    var password: String = ""

    /** SSH tunnel provider to use.
     * If set, all remote connections are routed through tunnels provided by this instance */
    var sshTunnelProvider: SshTunnelProvider? = null

    var archive: Boolean = true
    var verbose: Boolean = true
    var progress: Boolean = true
    var partial: Boolean = true
    var wholeFile: Boolean = false
    /** Skip files based on checksum, not on timestamp/size */
    var skipBasedOnChecksum: Boolean = true
    /** Fuzzy matching of compare/copy dests */
    var fuzzy: Boolean = true
    /** Preserve executable flag of files */
    var preserveExecutability = true
    /** Preserve acl permissions */
    var preserveAcls: Boolean = false
    /** Preserve permissions */
    var preservePermissions: Boolean? = null
    /** Preserve times */
    var preserveTimes: Boolean? = null
    /** Preserve groups */
    var preserveGroup: Boolean? = null
    /** Preserve owners */
    var preserveOwner: Boolean? = null
    /** Compression level, 0 (none) - 9 (max) */
    var compression: Int = 0
    /** Relative paths */
    var relativePaths: Boolean = false
    /** Delete destination files */
    var delete: Boolean = false
    /** Remove source files after successful transmission */
    var removeSourceFiles: Boolean = false
    /** List of relative destination paths to compare against */
    var comparisonDestinations: List<Rsync.URI> = ArrayList()
    /** List of relative destination paths to compare against and copy from (to minimize files to transfer) */
    var copyDestinations: List<Rsync.URI> = ArrayList()
    /** Use relative paths */
    var relative: Boolean = false
    /** IO timeout in seconds */
    var timeout: Int? = null

    class Result(val files: List<File>) {

    }

    //region Records
    /**
     * File entry record
     * @param line Line to parse
     */
    data class FileRecord(val flags: String, val path: String) {
        companion object {
            /** Output format for rsync */
            val OutputFormat = ">>> %i %n %L"

            /**
             * Try to parse file record line
             * @return FileRecord or null if it couldn't be parsed
             */
            fun tryParse(line: String): FileRecord? {
                // Flag field length is 12 on osx (linux?) and 11 on windows.
                var re = Regex("^>>> (.{11,12}) (.*)$")
                var mr = re.find(line) ?: return null

                return FileRecord(
                        flags = mr.groups[1]!!.value,
                        path = mr.groups[2]!!.value)
            }
        }

        init {
        }

        val isDirectory: Boolean
            get() = this.flags[1] == 'd'
    }

    /**
     * Progress record
     * @param line Line to parse
     */
    data class ProgressRecord(val bytes: Int, val percentage: Int) {
        companion object {
            /**
             * Try to parse progress record line
             * @return ProgressRecord or null if it couldn't be parsed
             */
            fun tryParse(line: String): ProgressRecord? {
                var re = Regex("^([0-9,]+)[\\s]+([0-9]+)%[\\s]+([^\\s]+).*$")
                var mr = re.find(line) ?: return null

                return ProgressRecord(
                        bytes = mr.groups[1]!!.value.replace(",", "").toInt(),
                        percentage = mr.groups[2]!!.value.toInt())
            }
        }
    }

    data class ListRecord(val flags: String, val size: Int, val timestamp: LocalDateTime, val filename: String) {
        companion object {
            /**
             * Try to parse list record line
             * @return ListRecord or null if it couldn't be parsed
             */
            fun tryParse(line: String): ListRecord? {
                // Example: drwxr-xr-x             10 2015/08/22 11:19:10 0.1
                var re = Regex("^([^\\s]{10})[\\s]+([0-9,]+)[\\s]+([0-9]{4})/([0-9]{2})/([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2}) ([^\\s]+)$")
                var mr = re.find(line) ?: return null

                return ListRecord(
                        flags = mr.groups[1]!!.value,
                        size = mr.groups[2]!!.value.replace(",", "").toInt(),
                        timestamp = LocalDateTime.of(
                                mr.groups[3]!!.value.toInt(),
                                mr.groups[4]!!.value.toInt(),
                                mr.groups[5]!!.value.toInt(),
                                mr.groups[6]!!.value.toInt(),
                                mr.groups[7]!!.value.toInt(),
                                mr.groups[8]!!.value.toInt()),
                        filename = mr.groups[9]!!.value)
            }
        }

        val isDirectory: Boolean
            get() = this.flags[0] == 'd'
    }
    //endregion

    /**
     * Helper to prepare tunneled connection, if required.
     * Returns the destination for rsync to connect to.
     * If no tunneled is configured for this client, will simply return the destination URI as a string
     * @param locations One or more rsync URIs, where 1 may optionally have a ssh tunnel instance attached
     * @param r Code block consuming the prepared/tunneled connections
     */
    private fun prepareTunnel(locations: Array<Rsync.URI>, r: (locations: Array<Rsync.URI>) -> Unit) {
        if (locations.count { l -> !l.isFile() } > 1)
            throw IllegalStateException("Only one rsync endpoint may be remote")

        val tunnelLocation = locations.firstOrNull { l -> !l.isFile() }
        val tunnelProvider = this.sshTunnelProvider

        if (tunnelProvider == null || tunnelLocation == null) {
            r(locations)
        } else {
            // Request tunnel to remote service
            val tunnel = tunnelProvider.request(
                    tunnelLocation.uri.host,
                    if (tunnelLocation.uri.port > 0) tunnelLocation.uri.port else 873)

            // Generate new locations, replacing remote host with localhost (tunnel).
            // Request tunnel connection in the process.
            val newLocations = locations.map { l ->
                val uri = l.uri

                if (!l.isFile()) {
                    // Mangle to localhost uri for connecting through tunnel
                    Rsync.URI(
                            URI(uri.scheme,
                                    uri.userInfo,
                                    "localhost",
                                    tunnel.localPort,
                                    uri.path,
                                    uri.query,
                                    uri.fragment))
                } else {
                    l
                }
            }.toTypedArray()

            r(newLocations)
        }
    }

    private fun prepareTunnel(location: Rsync.URI, r: (location: Rsync.URI) -> Unit) {
        this.prepareTunnel(arrayOf(location), { uris ->
            r(uris.get(0))
        })
    }

    /**
     * Adds common options to rsync command
     * @param command
     */
    private fun addCommonOptions(command: ArrayList<String>) {
        if (this.timeout != null)
            command.add("--timeout=${this.timeout!!}")
    }

    /**
     * List destination directory
     */
    fun list(uri: Rsync.URI): List<ListRecord> {
        var result = ArrayList<ListRecord>()

        this.prepareTunnel(uri, { uri ->
            var command = ArrayList<String>()

            command.add(Rsync.executable.file.toString())
            command.add("--list-only")

            this.addCommonOptions(command)

            command.add(uri.toString())

            log.debug(command.joinToString(" "))

            var pb = ProcessBuilder(command)

            // Set password via env var
            pb.environment().put("RSYNC_PASSWORD", this.password);

            val error = StringBuffer()
            var pe: ProcessExecutor = ProcessExecutor(pb,
                    outputHandler = object : ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true) {
                        override fun onProcessedOutput(output: String) {
                            var lr = ListRecord.tryParse(output)
                            if (lr != null) {
                                result.add(lr)
                            }
                        }
                    },
                    errorHandler = ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true, collectInto = error))

            pe.start()

            try {
                pe.waitFor()
            } catch(e: Exception) {
                if (error.length > 0) log.error(error.toString())
                throw e
            }
        })

        return result
    }

    /**
     * Synchronize
     * @return Sync result
     */
    @JvmOverloads fun sync(source: Rsync.URI,
                           destination: Rsync.URI,
                           onFile: ((fr: FileRecord) -> Unit)? = null,
                           onProgress: ((pr: ProgressRecord) -> Unit)? = null)
            : Result {

        var files = ArrayList<File>()

        this.prepareTunnel(arrayOf(source, destination), { uris ->
            val newSource = uris.get(0)
            val newDestination = uris.get(1)

            var command = ArrayList<String>()
            var infoFlags = ArrayList<String>()

            // Prepare command
            command.add(Rsync.executable.file.toString())

            if (this.verbose) command.add("-v")
            if (this.archive) command.add("-a")
            if (this.preserveExecutability) command.add("--executability")
            if (this.preserveAcls) command.add("-A")
            if (this.preservePermissions != null)
                command.add(if (this.preservePermissions!!) "--perms" else "--no-perms")
            if (this.preserveTimes != null)
                command.add(if (this.preserveTimes!!) "--times" else "--no-times")
            if (this.preserveGroup != null)
                command.add(if (this.preserveGroup!!) "--group" else "--no-group")
            if (this.preserveOwner != null)
                command.add(if (this.preserveOwner!!) "--owner" else "--no-owner")
            if (this.skipBasedOnChecksum) command.add("-c")
            if (this.fuzzy) command.add("-yy")
            if (this.relativePaths) command.add("-R")
            if (this.delete) command.add("--delete")
            if (this.removeSourceFiles) command.add("--remove-source-files")
            if (this.partial) command.add("--partial")
            if (this.relative) command.add("--relative")
            command.add(if (partial) "--whole-file" else "--no-whole-file")
            if (this.compression > 0) {
                command.add("-zz")
                command.add("--compress-level=${this.compression}")
            }
            for (url in this.comparisonDestinations) {
                command.add("--compare-dest")
                command.add(url.toString())
            }
            for (url in this.copyDestinations) {
                command.add("--copy-dest")
                command.add(url.toString())
            }

            this.addCommonOptions(command)

            // Info flags
            if (this.progress) infoFlags.add("progress2")

            if (infoFlags.size > 0)
                command.add("--info=${java.lang.String.join(",", infoFlags)}")

            command.add("--out-format=${FileRecord.OutputFormat}")

            command.add(newSource.toString())
            command.add(newDestination.toString())

            log.debug(command.joinToString(" "))

            // Prepare process builder
            var pb: ProcessBuilder = ProcessBuilder(command)

            // Set password via env var
            pb.environment().put("RSYNC_PASSWORD", this.password);

            // Execute
            val error = StringBuffer()

            var pe: ProcessExecutor = ProcessExecutor(pb,
                    outputHandler = object : ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true) {
                        override fun onProcessedOutput(output: String) {
                            var fr = FileRecord.tryParse(output)
                            if (fr != null) {
                                if (onFile != null) onFile(fr)
                                log.trace(fr)
                                if (!fr.isDirectory)
                                    files.add(File(fr.path))
                                return
                            }

                            var pr = ProgressRecord.tryParse(output)
                            if (pr != null) {
                                if (onProgress != null) onProgress(pr)
                                log.trace(pr)
                                return
                            }
                        }
                    },
                    errorHandler = ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true, collectInto = error))

            pe.start()

            try {
                pe.waitFor()
            } catch(e: Exception) {
                if (error.length > 0) log.error(error.toString())
                throw e
            }

            if (this.preservePermissions == false &&
                    destination.uri.scheme.compareTo("file", ignoreCase = true) == 0 &&
                    SystemUtils.IS_OS_WINDOWS) {
                // Cygwin's rsync implementation applies some crazy acls, even when setting preservation flags to false (sa.)
                PermissionUtil.applyAclRecursively(File(destination.uri).parentFile)
            }
        })

        return Result(files)
    }
}