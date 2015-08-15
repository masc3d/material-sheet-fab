package sx.rsync

import com.google.common.base.StandardSystemProperty
import com.google.common.base.Strings
import org.apache.commons.logging.LogFactory
import sx.ProcessExecutor
import java.io.BufferedWriter
import java.io.File
import java.io.OutputStreamWriter
import java.net.URI
import java.net.URL
import java.util.*

/**
 * Created by masc on 15.08.15.
 */
public class RsyncClient(path: File) : Rsync(path) {
    val log = LogFactory.getLog(this.javaClass)

    var source: URI? = null
    var destination: URI? = null

    var archive: Boolean = true
    var verbose: Boolean = true
    var progress: Boolean = true
    var partial: Boolean = true
    /** Compression level, 0 (none) - 9 (max) */
    var compression: Int = 0

    var password: String = ""

    public fun run() {
        if (this.source == null || this.destination == null)
            throw IllegalArgumentException("Source and destination are mandatory")

        var command = ArrayList<String>()
        var infoFlags = ArrayList<String>()

        command.add(this.path.toString())

        if (this.progress) infoFlags.add("progress2")
        
        if (this.archive) command.add("-a")
        if (this.verbose) command.add("-v")
        if (this.partial) command.add("--partial")
        if (this.compression > 0) {
            command.add("-zz")
            command.add("--compress-level=${this.compression}")
        }
        if (infoFlags.size() > 0)
            command.add("--info=${java.lang.String.join(",", infoFlags)}")

        command.add("--out-format=>>> %i %n %L")

        command.add(this.source.toString())
        command.add(this.destination.toString())

        log.trace("Command ${java.lang.String.join(" ", command)}")
        var pb: ProcessBuilder = ProcessBuilder(command)

        var output = StringBuffer()
        var error = StringBuffer()

        // Execute
        var pe: ProcessExecutor = ProcessExecutor(pb, object : ProcessExecutor.StreamHandler {
            override fun onOutput(o: String?) {
                var line = o?.trim()
                if (line == null || line.length() == 0)
                    return

                output.append(line + StandardSystemProperty.LINE_SEPARATOR.value())
                log.info(line)
            }

            override fun onError(o: String?) {
                var line = o?.trim()
                if (line == null || line.length() == 0)
                    return
                if (line.startsWith("Password:"))
                    return

                error.append(line + StandardSystemProperty.LINE_SEPARATOR.value())
                log.error(line)
            }
        })

        pe.start()

        // Write password to standard input
        var os = OutputStreamWriter(pe.getProcess().getOutputStream())
        os.write(this.password + StandardSystemProperty.LINE_SEPARATOR.value())
        os.flush()

        try {
            pe.waitFor()
        } catch(e: Exception) {
            this.log.error(e.getMessage(), e)
        }

//        if (output.length() > 0)
//            log.info(output.toString())
//        if (error.length() > 0)
//            log.error(error.toString())
    }
}