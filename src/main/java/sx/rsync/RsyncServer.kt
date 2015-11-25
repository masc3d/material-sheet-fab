package sx.rsync

import org.apache.commons.logging.LogFactory
import org.ini4j.Ini
import sx.Disposable
import sx.ProcessExecutor
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.attribute.*
import java.util.*

/**
 * Rsync server. Wraps rsync as a daemon process, generates configurations as needed
 * Created by masc on 15.08.15.
 */
class RsyncServer(
        /** Path containing configuration files */
        val configurationPath: File,
        /** Embedded configuration */
        val configuration: RsyncServer.Configuration? = null) : Rsync(), Disposable {

    val log = LogFactory.getLog(RsyncClient::class.java)
    var processExecutor: ProcessExecutor? = null

    /**
     * Rsync server configuration
     */
    class Configuration {
        companion object {
            val CONFIG_FILENAME = "rsyncd.conf"
            val SECRETS_FILENAME = "rsyncd.secrets"
        }
        private val log = LogFactory.getLog(this.javaClass)

        var useChroot: Boolean = false
        /** Controls whether the daemon performs a reverse lookup on the client’s IP address to determine its hostname */
        var reverseLookup: Boolean = true
        /** Controls  whether  the  daemon performs a forward lookup on any hostname specified in an hosts allow/deny setting */
        var forwardLookup: Boolean = true
        /** Rsync log file */
        var logFile: File? = null
        /** Rsync port */
        var port: Int? = null
        /** Rsync modules */
        val modules: ArrayList<Rsync.Module> = ArrayList<Rsync.Module>()

        /**
         * Save configuration files to path
         * @param path Path to save configuration files to
         */
        fun save(path: File) {
            var secretsFile = File(path, SECRETS_FILENAME)
            var configFile = File(path, CONFIG_FILENAME)

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

            // Amend secrets file permissions
            val nioSecretsFile = secretsFile.toPath()
            var aclFav = Files.getFileAttributeView(nioSecretsFile, AclFileAttributeView::class.java)
            if (aclFav != null) {
                var fowner = Files.getOwner(nioSecretsFile)
                val acls = ArrayList<AclEntry>()
                acls.add(AclEntry.newBuilder()
                        .setType(AclEntryType.ALLOW)
                        .setPrincipal(fowner)
                        .setPermissions(AclEntryPermission.values.toSet()).build())
                aclFav.acl = acls
            } else {
                var posixFav = Files.getFileAttributeView(nioSecretsFile, PosixFileAttributeView::class.java)
                if (posixFav != null) {
                    val perms = HashSet<PosixFilePermission>()
                    perms.add(PosixFilePermission.OWNER_READ)
                    perms.add(PosixFilePermission.OWNER_WRITE)
                    posixFav.setPermissions(perms)
                } else {
                    log.warn("Could not set secret file permissions")
                }
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

            var ini = Ini()
            var globalSection = ini.config.globalSectionName

            ini.config.isEmptySection = true
            ini.config.isGlobalSection = true
            ini.config.isEscape = false

            ini.put(globalSection, "use chroot", convertBoolean(this.useChroot))
            ini.put(globalSection, "reverse lookup", convertBoolean(this.reverseLookup))
            ini.put(globalSection, "forward lookup", convertBoolean(this.forwardLookup))
            if (this.port != null)
                ini.put(globalSection, "port", port!!.toString())
            if (this.logFile != null)
                ini.put(globalSection, "log file", Rsync.URI(this.logFile!!, asDirectory = false))

            for (module in this.modules) {
                var section = ini.add(module.name)
                section.add("path", Rsync.URI(module.path, asDirectory = false).toString())
                if (module.secretsFile != null)
                    section.add("secrets file", Rsync.URI(module.secretsFile!!, asDirectory = false).toString())
                section.add("auth users", module.permissions
                        .asSequence()
                        .map { entry -> "${entry.key}:${entry.value}" }
                        .joinToString(" "))
            }

            ini.store(os)
        }

        /**
         * Save secrets file
         * @param file File to save to
         */
        fun saveSecrets(file: File) {
            var os = FileOutputStream(file).buffered()
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
            var ow = PrintWriter(os)

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

        this.processExecutor = ProcessExecutor(ProcessBuilder(command),
                errorHandler = ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true, collectInto = error))
        this.processExecutor?.onTermination = { ex ->
            if (error.length > 0) this.log.error(error.toString())
            this.onTermination(ex)
        }
        this.processExecutor?.start()
    }

    /**
     * Stop rsync daemon
     */
    @Synchronized fun stop() {
        log.info("Stopping rsync server")
        if (this.processExecutor != null) {
            this.processExecutor?.close()
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