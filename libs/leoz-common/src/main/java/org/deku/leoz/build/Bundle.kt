package org.deku.leoz.build

import org.apache.commons.lang3.SystemUtils
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.function.BiPredicate
import kotlin.properties.Delegates
import kotlin.text.MatchResult
import kotlin.text.Regex

/**
 * Native packager bundle
 * Created by masc on 10.09.15.
 */
public class Bundle(
        public val name: String,
        public val os: OperatingSystem,
        public val basePath: File) {
    /** Bundle content path */
    public val contentPath: File
    /** Jar path */
    public val jarPath: File
    /** Bumdle configuration file */
    public val configFile: File
    public val configuration: Configuration by Delegates.lazy { -> Configuration() }

    init {
        val nioBasePath: Path = basePath.toPath()
        val nioContentPath: Path
        val nioJarPath: Path
        if (os == OperatingSystem.OSX) {
            nioContentPath = nioBasePath
                    .resolve(this.name + ".app")
                    .resolve("Contents")

            nioJarPath = nioContentPath
                    .resolve("Java")
        } else {
            nioContentPath = nioBasePath
            nioJarPath = nioContentPath.resolve("app")
        }

        this.contentPath = nioContentPath.toFile()
        this.jarPath = nioJarPath.toFile()

        var nioConfigFile = Files.find(nioJarPath, 1, BiPredicate { p, a -> a.isRegularFile() && p.getFileName().toString().endsWith(".cfg") }).findFirst()
        if (!nioConfigFile.isPresent())
            throw IllegalStateException("Config file not found within jar path [${jarPath}]")

        this.configFile = nioConfigFile.get().toFile()
    }

    companion object {
    }

    /**
     * Native packager bundle configuration (file)
     * Created by masc on 10.09.15.
     */
    public inner class Configuration() {
        private val KEY_APP_MAINJAR = "app.mainjar"
        private val KEY_APP_VERSION = "app.version"
        private val KEY_APP_CLASSPATH = "app.classpath"

        /** Contains key/value pairs or plain strings (non parsable lines) */
        private val entries = ArrayList<Any>()
        private val entryMap = LinkedHashMap<String, String>()

        /** Application main jar */
        public var appMainJar: String
            get() {
                return entryMap.get(KEY_APP_MAINJAR)
            }
            set(value: String) {
                entryMap.set(KEY_APP_MAINJAR, value)
            }

        public var appVersion: String
            get() {
                return entryMap.get(KEY_APP_VERSION)
            }
            set(value: String) {
                entryMap.set(KEY_APP_VERSION, value)
            }

        public var appClassPath: List<String>
            get() {
                return entryMap.get(KEY_APP_CLASSPATH).split(':', ';')
            }
            set(value: List<String>) {
                entryMap.set(KEY_APP_CLASSPATH, value.joinToString(";"))
            }

        /** c'tor. loads configuration from file */
        init {
            val reader = FileReader(this@Bundle.configFile).buffered()

            try {
                val re = Regex("^(app\\..+)=(.*)$")
                var line: String? = null

                val read = { line = reader.readLine(); line != null }
                while(read()) {
                    val entry: Any
                    val mr = re.match(line!!)
                    if (mr != null) {
                        entry = Pair(mr.groups.get(1)!!.value, mr.groups.get(2)!!.value)
                        entryMap. putAll(entry)
                    } else entry = line!!
                    entries.add(entry)
                }
            } finally {
                reader.close()
            }
        }

        /**
         * Save configuration
         */
        public fun save() {
            val writer = FileWriter(this@Bundle.configFile).buffered()

            try {
                for (entry in entries) {
                    val outputLine = if (entry is Pair<*, *>) {
                        // Get current entry value from entry map
                        val v = this.entryMap.get(entry.first)
                        "${entry.first}=${v}"
                    } else entry.toString()
                    writer.write(outputLine)
                    writer.write(if (os == OperatingSystem.WINDOWS) "\r\n" else "\n")
                }
            } finally {
                writer.close()
            }
        }
    }
}
