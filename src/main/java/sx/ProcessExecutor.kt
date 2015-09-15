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

    /** Indicates if process is stopping gracefully */
    private @volatile var stopping = false

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
         */
        fun onOutput(output: String)
    }

    /**
     * Default stream handler, collecting both error and output
     */
    public open class DefaultStreamHandler @jvmOverloads constructor(
            public val trim: Boolean = false,
            public val omitEmptyLines: Boolean = false,
            public val collectBuffer: StringBuffer? = null
    ) : StreamHandler {
        override fun onOutput(output: String) {
            // Optionally trim
            val processedOutput = if (this.trim) output.trim() else output

            // Optionally omit empty lines
            if (this.omitEmptyLines && processedOutput.length() == 0)
                return

            this.onProcessedOutput(processedOutput)

            // Optionally collect output
            if (collectBuffer != null)
                collectBuffer.append(processedOutput + StandardSystemProperty.LINE_SEPARATOR.value())
        }

        /**
         * Derived classes can override this method for handling processed output
         */
        protected open fun onProcessedOutput(output: String) { }
    }

    /**
     * Monitor thread
     */
    private inner class MonitorThread : Thread() {
        private volatile var shutdownHookInvoked = false

        override fun run() {
            val shutdownHook = object : Thread("ProcessExecutor shutdown hook") {
                override fun run() {
                    shutdownHookInvoked = true
                    if (process!!.isAlive()) {
                        log.warn("Terminating process [${processBuilder.command().get(0)}]")
                        process!!.destroy()
                    }
                }
            }

            Runtime.getRuntime().addShutdownHook(shutdownHook)

            var exception: Exception? = null
            try {
                var exitCode = process!!.waitFor()
                // Don't throw process exception if exit oode was non zero while the process was being
                // stopped/destroyed. (observed especially on windows)
                if (exitCode != 0 && !stopping)
                    exception = ProcessException(exitCode)
            } catch (e: InterruptedException) {
                log.error(e.getMessage(), e)
            } finally {
                if (!shutdownHookInvoked) {
                    Runtime.getRuntime().removeShutdownHook(shutdownHook)
                    shutdownHook.run()
                }
                this@ProcessExecutor.onTermination(exception)
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

    /** Termination callback */
    public var onTermination: (exception: Exception?) -> Unit = { }

    /**
     * Start process
     */
    public @synchronized fun start() {
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
    @throws(InterruptedException::class, ProcessException::class)
    public @synchronized fun waitFor() {
        if (process == null)
            throw IllegalStateException("Process not started")

        var returnCode: Int
        try {
            returnCode = process!!.waitFor()
        } finally {
            this.stop()
        }

        if (returnCode != 0)
            throw ProcessException(returnCode)
    }

    /**
     * Stop/destroy process
     */
    public @synchronized fun stop() {
        this.stopping = true

        if (process != null && process!!.isAlive()) {
            process!!.destroy()
        }

        // Wait for stream reader threads to terminate
        if (outputReaderThread != null) {
            try {
                outputReaderThread!!.join()
            } catch (e: InterruptedException) {
                log.warn(e.getMessage(), e)
            }
            outputReaderThread = null
        }
        if (errorReaderThread != null) {
            try {
                errorReaderThread!!.join()
            } catch (e: InterruptedException) {
                log.warn(e.getMessage(), e)
            }
            errorReaderThread = null
        }

        // Wait for monitor thread to terminate
        if (monitorThread != null) {
            try {
                monitorThread!!.join()
            } catch(e: InterruptedException) {
                log.warn(e.getMessage(), e)
            }
            monitorThread = null
        }

        process = null
        this.stopping = false
    }

    override fun dispose() {
        this.stop()
    }
}
