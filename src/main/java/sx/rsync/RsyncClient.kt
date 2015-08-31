package sx.rsync

import com.google.common.base.StandardSystemProperty
import com.google.common.base.Strings
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import sx.ProcessExecutor
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter
import java.lang
import java.net.URI
import java.net.URL
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*
import kotlin.text.Regex

/**
 * Created by masc on 15.08.15.
 */
public class RsyncClient() {
    companion object {
        val log = LogFactory.getLog(RsyncClient.javaClass)
    }

    /**
     * Rsync URI
     * @param uri File or rsync URI
     * @param includeDir Indicates if final path component/directory should be included (implies traling slash if false)
     */
    public class URI(uri: java.net.URI, val includeDir: Boolean = false) {
        val uri: java.net.URI

        init {
            // Make sure URI has trailing slash (or not) according to flag
            if (includeDir) {
                this.uri = if (uri.getPath().endsWith('/')) java.net.URI(uri.toString().trimEnd('/')) else uri
            } else {
                this.uri = if (!uri.getPath().endsWith('/')) java.net.URI(uri.toString() + '/') else uri
            }
        }

        /**
         * @param path Local path
         */
        constructor(path: java.nio.file.Path, includeDir: Boolean = false) : this(path.toUri(), includeDir) { }

        /**
         * @param uri URI string
         */
        constructor(uri: String, includeDir: Boolean = false) : this(java.net.URI(uri), includeDir) { }

        /**
         * @param file Local file
         */
        constructor(file: File, includeDir: Boolean = false) : this(file.toURI(), includeDir) { }

        // Extension methods for java.net.URI
        private fun java.net.URI.isFile(): Boolean {
            return this.getScheme() == "file"
        }

        /**
         * Resolve path
         */
        public fun resolve(str: String): URI {
            return RsyncClient.URI(
                    uri = if (uri.getPath().endsWith('/')) uri.resolve(str) else java.net.URI(uri.toString() + "/" + str),
                    includeDir = this.includeDir)
        }

        override fun toString(): String {
            if (!this.uri.isFile())
                return this.uri.toString()

            var rsyncPath: String
            if (SystemUtils.IS_OS_WINDOWS)
            // Return cygwin path on windows systems
                rsyncPath = "/cygdrive${this.uri.getPath().replace(":", "")}"
            else
                rsyncPath = Paths.get(this.uri).toAbsolutePath().toString()

            if (this.uri.getPath().endsWith('/'))
                rsyncPath += FileSystems.getDefault().getSeparator()

            return rsyncPath
        }
    }

    var source: RsyncClient.URI? = null
    var destination: RsyncClient.URI? = null

    /** Remote source/destination password */
    var password: String = ""

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
    var preserveAcls = true
    /** Compression level, 0 (none) - 9 (max) */
    var compression: Int = 0
    /** Relative paths */
    var relativePaths: Boolean = false
    /** Delete destination files */
    var delete: Boolean = false
    /** List of relative destination paths to compare against */
    var comparisonDestinations: List<java.net.URI> = ArrayList()
    /** List of relative destination paths to compare against and copy from (to minimize files to transfer) */
    var copyDestinations: List<java.net.URI> = ArrayList()

    public class Result(val files: List<File>) {

    }

    //region Records
    /**
     * File entry record
     * @param line Line to parse
     */
    public data class FileRecord(val flags: String, val path: String) {
        companion object {
            /** Output format for rsync */
            val OutputFormat = ">>> %i %n %L"

            /**
             * Try to parse file record line
             * @return FileRecord or null if it couldn't be parsed
             */
            public fun tryParse(line: String): FileRecord? {
                // Flag field length is 12 on osx (linux?) and 11 on windows.
                var re = Regex("^>>> (.{11,12}) (.*)$")
                var mr = re.match(line) ?: return null
                return FileRecord(
                        flags = mr.groups[1]!!.value,
                        path = mr.groups[2]!!.value)
            }
        }

        init {
        }

        public val isDirectory: Boolean
            get() = this.flags[1] == 'd'
    }

    /**
     * Progress record
     * @param line Line to parse
     */
    public data class ProgressRecord(val bytes: Int, val percentage: Int) {
        companion object {
            /**
             * Try to parse progress record line
             * @return ProgressRecord or null if it couldn't be parsed
             */
            public fun tryParse(line: String): ProgressRecord? {
                var re = Regex("^([0-9,]+)[\\s]+([0-9]+)%[\\s]+([^\\s]+).*$")
                var mr = re.match(line) ?: return null
                return ProgressRecord(
                        bytes = mr.groups[1]!!.value.replace(",", "").toInt(),
                        percentage = mr.groups[2]!!.value.toInt())
            }
        }
    }

    public data class ListRecord(val flags: String, val size: Int, val timestamp: LocalDateTime, val filename: String) {
        companion object {
            /**
             * Try to parse list record line
             * @return ListRecord or null if it couldn't be parsed
             */
            public fun tryParse(line: String): ListRecord? {
                // Example: drwxr-xr-x             10 2015/08/22 11:19:10 0.1
                var re = Regex("^([^\\s]{10})[\\s]+([0-9,]+)[\\s]+([0-9]{4})/([0-9]{2})/([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2}) ([^\\s]+)$")
                var mr = re.match(line) ?: return null

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

        public val isDirectory: Boolean
            get() = this.flags[0] == 'd'
    }
    //endregion

    /**
     * List destination directory
     */
    public fun list(): List<ListRecord> {
        if (this.destination == null)
            throw IllegalArgumentException("Destination is mandatory")

        var result = ArrayList<ListRecord>()

        var command = ArrayList<String>()

        command.add(Rsync.executablePath.toString())
        command.add("--list-only")
        command.add(this.destination.toString())

        var pb = ProcessBuilder(command)

        // Set password via env var
        pb.environment().put("RSYNC_PASSWORD", this.password);

        var pe: ProcessExecutor = ProcessExecutor(pb, object : ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true) {
            override fun onOutput(output: String): Boolean {
                if (super.onOutput(output)) {
                    var lr = ListRecord.tryParse(output)
                    if (lr != null) {
                        result.add(lr)
                    }
                    return true
                } else
                    return false
            }
        })

        pe.start()

        try {
            pe.waitFor()
        } catch(e: Exception) {
            log.error(e.getMessage(), e)
        }

        return result
    }

    /**
     * Synchronize
     * @return Sync result
     */
    @jvmOverloads public fun sync(
            fileRecordCallback: (fr: FileRecord) -> Unit = {},
            progressRecordCallback: (pr: ProgressRecord) -> Unit = {})
            : Result {

        if (this.source == null || this.destination == null)
            throw IllegalArgumentException("Source and destination are mandatory")

        var command = ArrayList<String>()
        var infoFlags = ArrayList<String>()

        // Prepare command
        command.add(Rsync.executablePath.toString())

        if (this.verbose) command.add("-v")
        if (this.archive) command.add("-a")
        if (this.preserveExecutability) command.add("-E")
        if (this.preserveAcls) command.add("-A")
        if (this.skipBasedOnChecksum) command.add("-c")
        if (this.fuzzy) command.add("-y")
        if (this.relativePaths) command.add("-R")
        if (this.delete) command.add("--delete")
        if (this.partial) command.add("--partial")
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

        // Info flags
        if (this.progress) infoFlags.add("progress2")

        if (infoFlags.size() > 0)
            command.add("--info=${java.lang.String.join(",", infoFlags)}")

        command.add("--out-format=${FileRecord.OutputFormat}")

        command.add(this.source!!.toString())
        command.add(this.destination!!.toString())

        log.trace("Command ${java.lang.String.join(" ", command)}")

        // Prepare process builder
        var pb: ProcessBuilder = ProcessBuilder(command)

        // Set password via env var
        pb.environment().put("RSYNC_PASSWORD", this.password);

        // Execute
        var error = StringBuffer()
        var files = ArrayList<File>()

        var pe: ProcessExecutor = ProcessExecutor(pb, object : ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true) {
            override fun onOutput(o: String): Boolean {
                if (super.onOutput(o)) {
                    var fr = FileRecord.tryParse(o)
                    if (fr != null) {
                        fileRecordCallback(fr)
                        log.trace(fr)
                        if (!fr.isDirectory)
                            files.add(File(fr.path))
                        return false
                    }

                    var pr = ProgressRecord.tryParse(o)
                    if (pr != null) {
                        progressRecordCallback(pr)
                        log.trace(pr)
                        return false
                    }
                    return true
                }
                return false
            }
        })

        pe.start()

        try {
            pe.waitFor()
        } catch(e: Exception) {
            log.error(e.getMessage(), e)
        }

        return Result(files)
    }
}