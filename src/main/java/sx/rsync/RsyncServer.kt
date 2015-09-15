package sx.rsync

import org.apache.commons.logging.LogFactory
import org.ini4j.Ini
import sx.Disposable
import sx.ProcessExecutor
import java.io.*
import java.nio.file.Files
import java.nio.file.attribute.*
import java.util
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by masc on 15.08.15.
 */
public class RsyncServer(
        /** Path containing configuration files */
        public val configurationPath: File,
        /** Embedded configuration */
        val configuration: RsyncServer.Configuration? = null) : Rsync(), Disposable {

    val log = LogFactory.getLog(RsyncClient.javaClass)
    var processExecutor: ProcessExecutor? = null

    /**
     * Rsync server configuration
     */
    public class Configuration {
        companion object {
            val CONFIG_FILENAME = "rsyncd.conf"
            val SECRETS_FILENAME = "rsyncd.secrets"
        }
        private val log = LogFactory.getLog(this.javaClass)

        public var useChroot: Boolean = false
        /** Rsync log file */
        public var logFile: File? = null
        /** Rsync port */
        public var port: Int? = null
        /** Rsync modules */
        public val modules: ArrayList<Rsync.Module> = ArrayList<Rsync.Module>()

        /**
         * Save configuration files to path
         * @param path Path to save configuration files to
         */
        public fun save(path: File) {
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

            val nioSecretsFile = secretsFile.toPath()
            var aclFav = Files.getFileAttributeView(nioSecretsFile, javaClass<AclFileAttributeView>())
            if (aclFav != null) {
                var fowner = Files.getOwner(nioSecretsFile)
                val acls = ArrayList<AclEntry>()
                acls.add(AclEntry.newBuilder().setPrincipal(fowner).setPermissions(AclEntryPermission.READ_DATA).build())
                aclFav.setAcl(acls)
            } else {
                var posixFav = Files.getFileAttributeView(nioSecretsFile, javaClass<PosixFileAttributeView>())
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
            var globalSection = ini.getConfig().getGlobalSectionName()

            ini.getConfig().setEmptySection(true)
            ini.getConfig().setGlobalSection(true)
            ini.getConfig().setEscape(false)
            ini.put(globalSection, "use chroot", convertBoolean(this.useChroot))
            if (this.port != null)
                ini.put(globalSection, "port", port!!.toString())
            if (this.logFile != null)
                ini.put(globalSection, "log file", Rsync.URI(this.logFile!!, asDirectory = false))

            for (module in this.modules) {
                var section = ini.add(module.name)
                section.add("path", module.path)
                if (module.secretsFile != null)
                    section.add("secrets file", Rsync.URI(module.secretsFile!!, asDirectory = false).toString())
                section.add("auth users", module.permissions
                        .asSequence()
                        .map { entry -> "${entry.getKey()}:${entry.getValue()}" }
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
                    .flatMap { m -> m.permissions.keySet().asSequence() }
                    .filterIsInstance<Rsync.User>()
                    .distinct()
                    .forEach { u -> ow.println("${u.name}:${u.password}") }

            ow.flush()
        }
    }

    /**
     * Start rsync daemon
     */
    public @synchronized fun start() {
        if (this.configuration != null) {
            // Running with embedded configuration

            // Store configuration to file
            this.configuration.save(this.configurationPath)

            // Remove log file if it exists
            if (this.configuration.logFile != null && this.configuration.logFile!!.exists())
                this.configuration.logFile!!.delete()
        }

        val command = ArrayList<String>()

        command.add(Rsync.executableFile.toString())
        command.add("--daemon")
        command.add("--no-detach")
        command.add("--config")
        command.add(Rsync.URI(File(this.configurationPath, Configuration.CONFIG_FILENAME), asDirectory = false).toString())

        val error = StringBuffer()

        this.processExecutor = ProcessExecutor(ProcessBuilder(command),
                errorHandler = ProcessExecutor.DefaultStreamHandler(trim = true, omitEmptyLines = true, collectBuffer = error))
        this.processExecutor?.onTermination = { ex ->
            if (error.length() > 0) this.log.error(error.toString())
            this.onTermination(ex)
        }
        this.processExecutor?.start()
    }

    /**
     * Stop rsync daemon
     */
    public @synchronized fun stop() {
        if (this.processExecutor != null) {
            this.processExecutor?.dispose()
            this.processExecutor = null
        }
    }

    /**
     * Wait for daemon to terminate
     */
    public fun waitFor() {
        this.processExecutor?.waitFor()
    }

    /**
     * Termination callback
     */
    public var onTermination: (exception: Exception?) -> Unit = { }

    override fun dispose() {
        this.stop();
    }
}