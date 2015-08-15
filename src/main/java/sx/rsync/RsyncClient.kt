package sx.rsync

import com.google.common.base.StandardSystemProperty
import org.apache.commons.logging.LogFactory
import sx.ProcessExecutor
import java.io.File
import java.net.URL

/**
 * Created by masc on 15.08.15.
 */
public class RsyncClient(path: File) : Rsync(path) {
    val log = LogFactory.getLog(this.javaClass)

    var source: URL? = null
    var destination: URL? = null

    var archive: Boolean = true
    var verbose: Boolean = true
    var progress: Boolean = true
    var partial: Boolean = true

    public fun run() {
        var output = StringBuffer()
        var error = StringBuffer()

        var pb: ProcessBuilder = ProcessBuilder(this.path.toString(),
                "-a",
                "-v")

        // Execute
        var pe: ProcessExecutor = ProcessExecutor(pb, object : ProcessExecutor.StreamHandler {
            override fun onError(o: String?) {
                output.append(o + StandardSystemProperty.LINE_SEPARATOR.value())
            }

            override fun onOutput(o: String?) {
                error.append(o + StandardSystemProperty.LINE_SEPARATOR.value())
            }
        })

        pe.start()
        try {
            pe.waitFor()
        } catch(e: Exception) {
            this.log.error(e.getMessage(), e)
        }

        if (output.length() > 0)
            log.info(output.toString())
        if (error.length() > 0)
            log.info(error.toString())
    }
}