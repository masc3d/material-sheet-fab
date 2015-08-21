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
import java.util.*
import kotlin.text.Regex

/**
 * Created by masc on 15.08.15.
 */
public class RsyncClient(path: File) : Rsync(path) {
    companion object {
        val log = LogFactory.getLog(RsyncClient.javaClass)
    }

    private fun URI.isFile(): Boolean {
        return this.getScheme() == "file"
    }

    private fun URI.toRsyncPath(): String {
        if (SystemUtils.IS_OS_WINDOWS)
            // Return cygwin path on windows systems
            return if (this.isFile()) "/cygdrive${this.getPath().replace(":", "")}" else this.toString()
        else
            return if (this.isFile()) File(this).getAbsolutePath() else this.toString()
    }

    var source: URI? = null
    var destination: URI? = null

    var archive: Boolean = true
    var verbose: Boolean = true
    var progress: Boolean = true
    var partial: Boolean = true
    var wholeFile: Boolean = false
    var skipBasedOnChecksum: Boolean = true
    var fuzzy: Boolean = true
    var preserveExecutability = true
    var preserveAcls = true
    /** Compression level, 0 (none) - 9 (max) */
    var compression: Int = 0

    var password: String = ""

    public class Result(val files: List<File>) {

    }

    /**
     * File entry record
     * @param line Line to parse
     */
    private data class FileRecord(val flags: String, val path: String) {
        companion object {
            /** Output format for rsync */
            val OutputFormat = ">>> %i %n %L"

            public fun tryParse(line: String): FileRecord? {
                // Flag field length is 12 on osx (linux?) and 11 on windows.
                var re = Regex("^>>> (.{11,12}) (.*)$")
                var mr = re.match(line) ?: return null
                return FileRecord(
                        flags = mr.groups[1]?.value ?: "",
                        path = mr.groups[2]?.value ?: "")
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
    private data class ProgressRecord(val bytes: Int, val percentage: Int) {
        companion object {
            public fun tryParse(line: String): ProgressRecord? {
                var re = Regex("^([0-9,]+)[\\s]+([0-9]+)%[\\s]+([^\\s]+).*$")
                var mr = re.match(line) ?: return null
                return ProgressRecord(
                        bytes = mr.groups[1]?.value?.replace(",", "")?.toInt() ?: 0,
                        percentage = mr.groups[2]?.value?.toInt() ?: 0)
            }
        }
    }

    /**
     * Synchronize
     * @return Sync result
     */
    public fun sync(
            fileRecordCallback: (fr: FileRecord) -> Unit = {},
            progressRecordCallback: (pr: ProgressRecord) -> Unit = {})
            : Result {
        if (this.source == null || this.destination == null)
            throw IllegalArgumentException("Source and destination are mandatory")

        var command = ArrayList<String>()
        var infoFlags = ArrayList<String>()

        // Prepare command
        command.add(this.path.toString())

        if (this.verbose) command.add("-v")
        if (this.archive) command.add("-a")
        if (this.preserveExecutability) command.add("-E")
        if (this.preserveAcls) command.add("-A")
        if (this.skipBasedOnChecksum) command.add("-c")
        if (this.fuzzy) command.add("-y")
        if (this.partial) command.add("--partial")
        command.add(if (partial) "--whole-file" else "--no-whole-file")
        if (this.compression > 0) {
            command.add("-zz")
            command.add("--compress-level=${this.compression}")
        }

        // Info flags
        if (this.progress) infoFlags.add("progress2")

        if (infoFlags.size() > 0)
            command.add("--info=${java.lang.String.join(",", infoFlags)}")

        command.add("--out-format=${FileRecord.OutputFormat}")

        command.add(this.source!!.toRsyncPath())
        command.add(this.destination!!.toRsyncPath())

        log.trace("Command ${java.lang.String.join(" ", command)}")

        // Prepare process builder
        var pb: ProcessBuilder = ProcessBuilder(command)

        // Set password via env var
        pb.environment().put("RSYNC_PASSWORD", this.password);

        // Execute
        var output = StringBuffer()
        var error = StringBuffer()
        var files = ArrayList<File>()

        var process: Process? = null
        var pe: ProcessExecutor = ProcessExecutor(pb, object : ProcessExecutor.StreamHandler {
            override fun onOutput(o: String?) {
                var line = o?.trim()
                if (line == null || line.length() == 0)
                    return

                var fr = FileRecord.tryParse(line)
                if (fr != null) {
                    fileRecordCallback(fr)
                    log.trace(fr)
                    if (!fr.isDirectory)
                        files.add(File(fr.path))
                    return
                }

                var pr = ProgressRecord.tryParse(line)
                if (pr != null) {
                    progressRecordCallback(pr)
                    log.trace(pr)
                    return
                }
                output.append(line + StandardSystemProperty.LINE_SEPARATOR.value())
                log.trace(line)
            }

            override fun onError(o: String?) {
                var line = o?.trim()
                if (line == null || line.length() == 0)
                    return

                error.append(line + StandardSystemProperty.LINE_SEPARATOR.value())
                log.error(line)
            }
        })

        pe.start()
        process = pe.getProcess()

        try {
            pe.waitFor()
        } catch(e: Exception) {
            log.error(e.getMessage(), e)
        }

        return Result(files)
    }
}