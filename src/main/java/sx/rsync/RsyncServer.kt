package sx.rsync

import org.ini4j.Ini
import sx.Disposable
import sx.ProcessExecutor
import java.io.*
import java.nio.file.attribute.UserPrincipal
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by masc on 15.08.15.
 */
public class RsyncServer() : Rsync(), Disposable {
    var processExecutor: ProcessExecutor? = null
    /** Config directory (where generated configuraiton is stored) */
    var configurationFilePath: File by Delegates.notNull()

    /**
     * Rsync server configuration
     */
    public class Configuration {
        public enum class Permission(val permission: String) {
            READONLY("ro"),
            READWRITE("rw"),
            DENY("deny");

            override fun toString(): String {
                return this.permission
            }
        }

        /** Rsync server module, equivalent to a shared folder */
        public data class Module(
                /** Module name */
                var name: String,
                /** The shared folder this module refers to */
                val path: File) {

            /**
             * Permissions for this module
             */
            public val permissions: HashMap<Principal, Permission> = HashMap()
        }

        public open class Principal(val name: String)

        /** Rsync user */
        public class User(
                name: String,
                val password: String) : Principal(name) {

            override fun toString(): String {
                return this.name
            }
        }

        public val useChroot: Boolean = true
        /** Rsync modules */
        public val modules: ArrayList<Module> = ArrayList<Module>()

        /**
         * Save configuration file
         * @param file File to save to
         */
        public fun save(file: File) {
            var os = FileOutputStream(file).buffered()
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
        public fun save(os: OutputStream) {
            fun convertBoolean(b: Boolean): String = if (b) "yes" else "no"

            var ini = Ini()
            ini.getConfig().setEmptySection(true)
            ini.getConfig().setGlobalSection(true)
            ini.getConfig().setEscape(false)
            ini.put(ini.getConfig().getGlobalSectionName(), "use chroot", convertBoolean(this.useChroot))

            for (module in this.modules) {
                var section = ini.add(module.name)
                section.add("path", module.path)
                section.add("auth users", module.permissions
                        .asSequence()
                        .map { entry -> "${entry.getKey()}:${entry.getValue()}" }
                        .joinToString(" "))
            }

            ini.store(os)
        }

        public fun saveSecrets(os: OutputStream) {
            var ow = PrintWriter(os)

            var users =  this.modules.asSequence()
                    .flatMap { m -> m.permissions.keySet().asSequence() }
                    .filterIsInstance<User>()
                    .distinct()
                    .forEach { u -> ow.println("${u.name}:${u.password}") }

            ow.flush()
        }
    }

    public @synchronized fun start() {
        val command = ArrayList<String>()

        command.add(Rsync.executablePath.toString())
        command.add("--daemon")
        command.add("--no-detach")

        this.processExecutor = ProcessExecutor(ProcessBuilder(command))
        this.processExecutor?.start()
    }

    public @synchronized fun stop() {
        if (this.processExecutor != null) {
            this.processExecutor?.dispose()
            this.processExecutor = null
        }
    }

    override fun dispose() {
        this.stop();
    }
}