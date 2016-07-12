package sx

import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Process executor with threaded stream reading support.
 * Unlike with process builder, process executor will attempt to destroy the process instance when it's disposed
 * or the jvm shutdowns down.
 * @param processBuilder Process builder
 * @param terminationTimeout Timeout on graceful termination before terminating process forcibly.
 * This timeout is applied when the process is terminated passively via shutdown hook or actively
 * when calling the stop method.
 * @param outputHandler Stream handler implementation
 */
class ProcessExecutor @JvmOverloads constructor(
        private val processBuilder: ProcessBuilder,
        private val terminationTimeout: Duration = Duration.ofSeconds(10),
        private val outputHandler: ProcessExecutor.StreamHandler = ProcessExecutor.DefaultStreamHandler(),
        private val errorHandler: ProcessExecutor.StreamHandler = ProcessExecutor.DefaultStreamHandler())
:
        Disposable
{
    private val log = LoggerFactory.getLogger(this.javaClass)
    var process: Process? = null
        private set
    private var outputReaderThread: StreamReaderThread? = null
    private var errorReaderThread: StreamReaderThread? = null
    private var monitorThread: MonitorThread? = null

    /** Indicates if process is stopping gracefully */
    private @Volatile var stopping = false

    /**
     * Process exception
     */
    inner class ProcessException(val errorCode: Int) : java.lang.Exception("Process failed with error code [${errorCode}]")

    /**
     * Stream handler interface
     */
    interface StreamHandler {
        /**
         * Called for each line of output.
         */
        fun onOutput(output: String)
    }

    /**
     * Default stream handler, collecting both error and output
     */
    open class DefaultStreamHandler @JvmOverloads constructor(
            val trim: Boolean = false,
            val omitEmptyLines: Boolean = false,
            val collectInto: StringBuffer? = null
    ) : StreamHandler {
        override fun onOutput(output: String) {
            // Optionally trim
            val processedOutput = if (this.trim) output.trim() else output

            // Optionally omit empty lines
            if (this.omitEmptyLines && processedOutput.length == 0)
                return

            this.onProcessedOutput(processedOutput)

            // Optionally collect output
            if (collectInto != null) {
                if (collectInto.length > 0)
                    collectInto.append(StandardSystemProperty.LINE_SEPARATOR.value())
                collectInto.append(processedOutput)
            }
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
        private @Volatile var shutdownHookInvoked = false

        override fun run() {
            val shutdownHook = object : Thread("ProcessExecutor shutdown hook") {
                override fun run() {
                    shutdownHookInvoked = true
                    process!!.destroyReliably(terminationTimeout)
                }
            }

            try {
                Runtime.getRuntime().addShutdownHook(shutdownHook)
            } catch(e: Throwable) {
                // Adding shutdown hook may fail if jvm is in the process of shutting down
                log.error(e.message, e)
            }

            var exception: Exception? = null
            try {
                var exitCode = process!!.waitFor()
                // Don't throw process exception if exit oode was non zero while the process was being
                // stopped/destroyed. (observed especially on windows)
                if (exitCode != 0 && !stopping && !shutdownHookInvoked)
                    exception = ProcessException(exitCode)
            } catch (e: InterruptedException) {
                log.error(e.message, e)
            } finally {
                if (!shutdownHookInvoked) {
                    try {
                        Runtime.getRuntime().removeShutdownHook(shutdownHook)
                    } catch (e: Throwable) {
                        // Removing shutdown hook may fail if jvm is in the process of shutting down
                        log.error(e.message, e)
                    }
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
                    log.error(ex.message, ex)
            }
        }
    }

    /** Termination callback */
    var onTermination: (exception: Exception?) -> Unit = { }

    /**
     * Start process
     */
    @Synchronized fun start() {
        if (process != null)
            throw IllegalStateException("Process already started")

        // Start process
        process = processBuilder.start()
        monitorThread = MonitorThread()
        monitorThread!!.start()

        // Add stream handlers
        outputReaderThread = StreamReaderThread(process!!.inputStream, outputHandler)

        errorReaderThread = StreamReaderThread(process!!.errorStream, errorHandler)

        outputReaderThread!!.start()
        errorReaderThread!!.start()
    }

    /**
     * Wait for process and stream reader threads to terminate
     * @return
     * *
     * @throws InterruptedException
     */
    @Throws(InterruptedException::class, ProcessException::class)
    @Synchronized fun waitFor() {
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
     * Destroys a process reliably.
     * Attempts to terminate the process gracefully, but will destroy forcible after timeout elapsed
     */
    fun Process.destroyReliably(timeout: Duration) {
        if (this.isAlive) {
            val processExecutable = processBuilder.command().get(0)
            log.info("Terminating process gracefully [${processExecutable}]")
            this.destroy()
            if (!this.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
                log.warn("Failed to terminate gracefully, destroying forcibly [${processExecutable}")
                this.destroyForcibly()
                this.waitFor()
                log.info("Process terminated forcibly [${processExecutable}]")
            } else {
                log.info("Process terminated gracefully [${processExecutable}]")
            }
        }
    }

    /**
     * Stop/destroy process
     */
    @Synchronized fun stop() {
        this.stopping = true

        if (process != null) {
            process!!.destroyReliably(terminationTimeout)
        }

        // Wait for stream reader threads to terminate
        if (outputReaderThread != null) {
            try {
                outputReaderThread!!.join()
            } catch (e: InterruptedException) {
                log.warn(e.message, e)
            }
            outputReaderThread = null
        }
        if (errorReaderThread != null) {
            try {
                errorReaderThread!!.join()
            } catch (e: InterruptedException) {
                log.warn(e.message, e)
            }
            errorReaderThread = null
        }

        // Wait for monitor thread to terminate
        if (monitorThread != null) {
            try {
                monitorThread!!.join()
            } catch(e: InterruptedException) {
                log.warn(e.message, e)
            }
            monitorThread = null
        }

        process = null
        this.stopping = false
    }

    override fun close() {
        this.stop()
    }
}
