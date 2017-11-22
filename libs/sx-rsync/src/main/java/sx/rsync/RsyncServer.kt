package sx.rsync

import jnr.posix.POSIXFactory
import org.apache.commons.lang3.SystemUtils
import org.ini4j.Ini
import org.slf4j.LoggerFactory
import sx.Disposable
import sx.ProcessExecutor
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.util.*

/**
 * Rsync server. Wraps rsync as a daemon process, generates configurations as needed
 * Created by masc on 15.08.15.
 */
class RsyncServer(
        /** Path containing configuration files */
        val configurationPath: File,
        /** Embedded configuration */
        val configuration: RsyncServer.Configuration? = null) : Disposable {

    val log = LoggerFactory.getLogger(RsyncClient::class.java)
    var processExecutor: ProcessExecutor? = null

    /**
     * Rsync server configuration
     */
    class Configuration {
        companion object {
            val CONFIG_FILENAME = "rsyncd.conf"
            val SECRETS_FILENAME = "rsyncd.secrets"
        }
        private val log = LoggerFactory.getLogger(this.javaClass)

        /** Bind address */
        var address: String? = null
        /** Use jaled root */
        var useChroot: Boolean = false
        /** Controls whether the daemon performs a reverse lookup on the client’s IP address to determine its hostname */
        var reverseLookup: Boolean = true
        /** Controls  whether  the  daemon performs a forward lookup on any hostname specified in an hosts allow/deny setting */
        var forwardLookup: Boolean = true
        /** Rsync log file */
        var logFile: File? = null
        /** Rsync port */
        var port: Int? = null
        /** Strict modes */
        var strictModes: Boolean = false
        /** Rsync modules */
        val modules: ArrayList<Rsync.Module> = ArrayList<Rsync.Module>()

        /**
         * Save configuration files to path
         * @param path Path to save configuration files to
         */
        fun save(path: File) {
            val secretsFile = File(path, SECRETS_FILENAME)
            val configFile = File(path, CONFIG_FILENAME)

            // Set secrets file for modules where it's not set
            this.modules.asSequence().filter { m -> m.secretsFile == null }.forEach { m ->
                m.secretsFile = secretsFile
            }

            // Save secrets file
            var os = FileOutputStream(secretsFile).buffered()
            try {
                this.saveSecrets(os)
            } finally {
                os.close()
            }

            // Save configuration file
            os = FileOutputStream(configFile).buffered()
            try {
                this.save(os)
            } finally {
                os.close()
            }
        }

        /**
         * Save configuration to output stream
         * @param os Output stream
         */
        fun save(os: OutputStream) {
            fun convertBoolean(b: Boolean): String = if (b) "yes" else "no"

            val ini = Ini()
            val globalSection = ini.config.globalSectionName

            ini.config.isEmptySection = true
            ini.config.isGlobalSection = true
            ini.config.isEscape = false

            if (this.address != null)
                ini.put(globalSection, "address", this.address!!)
            ini.put(globalSection, "use chroot", convertBoolean(this.useChroot))
            ini.put(globalSection, "reverse lookup", convertBoolean(this.reverseLookup))
            ini.put(globalSection, "forward lookup", convertBoolean(this.forwardLookup))
            ini.put(globalSection, "strict modes", convertBoolean(this.strictModes))
            if (this.port != null)
                ini.put(globalSection, "port", port!!.toString())
            if (this.logFile != null)
                ini.put(globalSection, "log file", Rsync.URI(this.logFile!!, asDirectory = false))

            for (module in this.modules) {
                val section = ini.add(module.name)

                section.add("path", Rsync.URI(module.path, asDirectory = false))

                if (module.secretsFile != null)
                    section.add("secrets file", Rsync.URI(module.secretsFile!!, asDirectory = false))

                section.add("auth users", module.permissions
                        .asSequence()
                        .map { entry -> "${entry.key}:${entry.value}" }
                        .joinToString(" ") as Any)

                if (SystemUtils.IS_OS_UNIX) {
                    POSIXFactory.getJavaPOSIX().also { posix ->
                        section.add("uid", posix.getuid())
                    }
                }
            }

            ini.store(os)
        }

        /**
         * Save secrets file
         * @param file File to save to
         */
        fun saveSecrets(file: File) {
            val os = FileOutputStream(file).buffered()
            try {
                this.saveSecrets(os)
            } finally {
                os.close()
            }
        }

        /**
         * Save secrets to output stream
         * @param os Output stream
         */
        fun saveSecrets(os: OutputStream) {
            val ow = PrintWriter(os)

            this.modules.asSequence()
                    .flatMap { m -> m.permissions.keys.asSequence() }
                    .filterIsInstance<Rsync.User>()
                    .distinct()
                    .forEach { u -> ow.println("${u.name}:${u.password}") }

            ow.flush()
        }
    }

    /**
     * Start rsync daemon
     */
    @Synchronized fun start() {
        log.info("Starting rsync server")
        if (this.configuration != null) {
            // Running with embedded configuration

            // Store configuration to file
            this.configuration.save(this.configurationPath)

            // Remove log file if it exists
            if (this.configuration.logFile != null && this.configuration.logFile!!.exists())
                this.configuration.logFile!!.delete()
        }

        val command = ArrayList<String>()

        command.add(Rsync.executable.file.toString())
        command.add("--daemon")
        command.add("--no-detach")
        command.add("--config")
        command.add(Rsync.URI(File(this.configurationPath, Configuration.CONFIG_FILENAME), asDirectory = false).toString())

        val error = StringBuffer()

        val processExecutor = ProcessExecutor(
                ProcessBuilder(command),
                errorHandler = ProcessExecutor.DefaultTextStreamHandler(trim = true, omitEmptyLines = true, collectInto = error))

        processExecutor.onTermination = { ex ->
            if (error.isNotEmpty()) this.log.error("${error}")
            this.onTermination(ex)
        }
        processExecutor.start()
        this.processExecutor = processExecutor
    }

    /**
     * Stop rsync daemon
     */
    @Synchronized fun stop() {
        log.info("Stopping rsync server")

        val processExecutor = this.processExecutor
        if (processExecutor != null) {
            processExecutor.close()
            this.processExecutor = null
        }
    }

    /**
     * Wait for daemon to terminate
     */
    fun waitFor() {
        this.processExecutor?.waitFor()
    }

    /**
     * Termination callback
     */
    var onTermination: (exception: Exception?) -> Unit = { }

    override fun close() {
        this.stop();
    }
}