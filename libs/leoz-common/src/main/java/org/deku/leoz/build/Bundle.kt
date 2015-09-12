package org.deku.leoz.build

import org.apache.commons.lang3.SystemUtils
import org.ini4j.Ini
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.BiPredicate
import kotlin.properties.Delegates

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
    public val configuration: Configuration by Delegates.lazy {() -> Configuration() }

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
        val SECTION_APPLICATION = "Application"
        val VALUE_APP_MAINJAR = "app.mainjar"

        /** Ini */
        private val ini: Ini

        /** Application main jar */
        public var appMainJar: String
            get() {
                return ini.get(SECTION_APPLICATION, VALUE_APP_MAINJAR)
            }
            set(value: String) {
                ini.put(SECTION_APPLICATION, VALUE_APP_MAINJAR, value)
            }

        /** c'tor. loads configuration from file */
        init {

            ini = Ini(this@Bundle.configFile)
            val config = ini.getConfig()
            config.setEscape(false)
            config.setEmptySection(true)
            config.setEmptyOption(true)

            ini.toList().forEach { println("${it.first} -> ${it.second}") }
        }

        /**
         * Save configuration
         */
        public fun save() {
            ini.store()
        }
    }
}
