package sx

import com.google.common.base.StandardSystemProperty
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.logging.StreamHandler

/**
 * Process executor with threaded stream reading support
 * @param processBuilder Process builder
 * @param outputHandler Stream handler implementation
 */
public class ProcessExecutor @jvmOverloads constructor(
        private val processBuilder: ProcessBuilder,
        private val outputHandler: ProcessExecutor.StreamHandler = ProcessExecutor.DefaultStreamHandler(),
        private val errorHandler: ProcessExecutor.StreamHandler = ProcessExecutor.DefaultStreamHandler()) : Disposable {

    private val log = LogFactory.getLog(this.javaClass)
    public var process: Process? = null
        private set
    private var outputReaderThread: StreamReaderThread? = null
    private var errorReaderThread: StreamReaderThread? = null
    private var monitorThread: MonitorThread? = null

    /**
     * Process exception
     */
    public inner class ProcessException(public val errorCode: Int) : java.lang.Exception("Process failed with error code [${errorCode}]")

    /**
     * Stream handler interface
     */
    public interface StreamHandler {
        /**
         * Called for each line of output.
         * @return true if output was successfully processed, false otherwise
         */
        public fun onOutput(output: String): Boolean
    }

    /**
     * Default stream handler, collecting both error and output
     */
    public open class DefaultStreamHandler @jvmOverloads constructor(
            public val trim: Boolean = false,
            public val omitEmptyLines: Boolean = false,
            public val collectBuffer: StringBuffer? = null
    ) : StreamHandler {
        override fun onOutput(output: String): Boolean {
            // Optionally trim
            val line = if (this.trim) output.trim() else output

            // Optionally omit empty lines
            if (this.omitEmptyLines && line.length() == 0)
                return false

            // Optionally collect output
            if (collectBuffer != null)
                collectBuffer.append(line + StandardSystemProperty.LINE_SEPARATOR.value())

            return true
        }
    }

    /**
     * Monitor thread
     */
    private inner class MonitorThread : Thread() {
        override fun run() {
            val shutdownHook = object : Thread("ProcessExecutor shutdown hook") {
                override fun run() {
                    if (process!!.isAlive()) {
                        log.warn("Terminating process [${processBuilder.command().get(0)}]")
                        process!!.destroy()
                    }
                }
            }

            Runtime.getRuntime().addShutdownHook(shutdownHook)

            try {
                process!!.waitFor()
            } catch (e: InterruptedException) {
                log.error(e.getMessage(), e)
            } finally {
                Runtime.getRuntime().removeShutdownHook(shutdownHook)
                shutdownHook.run()
            }
        }
    }

    /**
     * Stream reader thread
     * @param stream Stream to read from
     * @param action Action to perform on each line
     */
    private inner class StreamReaderThread(
            var stream: InputStream,
            var action: StreamHandler) : Thread() {

        var reader: BufferedReader

        init {
            reader = BufferedReader(InputStreamReader(stream))
        }

        override fun run() {
            try {
                var line: String? = null
                while ( { line = reader.readLine(); line }() != null) {
                    action.onOutput(line!!)
                }
            } catch (ex: Exception) {
                if (ex !is InterruptedException)
                    log.error(ex.getMessage(), ex)
            }
        }
    }

    /**
     * Start process
     * @throws IOException
     */
    @throws(IOException::class)
    public fun start() {
        if (process != null)
            throw IllegalStateException("Process already started")

        // Start process
        process = processBuilder.start()
        monitorThread = MonitorThread()
        monitorThread!!.start()

        // Add stream handlers
        outputReaderThread = StreamReaderThread(process!!.getInputStream(), outputHandler)

        errorReaderThread = StreamReaderThread(process!!.getErrorStream(), errorHandler)

        outputReaderThread!!.start()
        errorReaderThread!!.start()
    }

    /**
     * Wait for process and stream reader threads to terminate
     * @return
     * *
     * @throws InterruptedException
     */
    throws(InterruptedException::class, ProcessException::class)
    public fun waitFor() {
        if (process == null)
            throw IllegalStateException("Process not started")

        var returnCode: Int
        try {
            returnCode = process!!.waitFor()
        } finally {
            if (process!!.isAlive()) {
                process!!.destroy()
            }

            // Wait for stream reader threads to terminate
            if (outputReaderThread != null) {
                outputReaderThread!!.join()
                outputReaderThread = null
            }
            if (errorReaderThread != null) {
                errorReaderThread!!.join()
                errorReaderThread = null
            }
        }

        if (returnCode != 0)
            throw ProcessException(returnCode)
    }

    override fun dispose() {
        if (process != null && process!!.isAlive()) {
            process!!.destroy()
        }
        process = null

        if (outputReaderThread != null) {
            outputReaderThread!!.interrupt()
            try {
                outputReaderThread!!.join()
            } catch (e: InterruptedException) {
                log.error(e.getMessage(), e)
            }

            outputReaderThread = null
        }

        if (errorReaderThread != null) {
            errorReaderThread!!.interrupt()
            try {
                errorReaderThread!!.join()
            } catch (e: InterruptedException) {
                log.error(e.getMessage(), e)
            }

            errorReaderThread = null
        }
    }
}
