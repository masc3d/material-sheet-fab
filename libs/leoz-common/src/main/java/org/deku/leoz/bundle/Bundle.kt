package org.deku.leoz.bundle

import com.google.common.hash.Hashing
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import org.deku.leoz.system.ProcessElevation
import sx.EmbeddedExecutable
import sx.ProcessExecutor
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import java.io.*
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.function.BiPredicate
import java.util.regex.Pattern
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import kotlin.text.Regex


/**
 * Represents a local/physical leoz bundle including a manifest containing metadata
 * Created by masc on 22.08.15.
 */
@XmlRootElement
class Bundle : Serializable {
    private val log = LogFactory.getLog(this.javaClass)

    /** Bundle path */
    var path: File? = null
        private set

    /** Bundle name */
    @XmlAttribute
    var name: String? = null
        private set

    /** Bundle version */
    @XmlAttribute
    @XmlJavaTypeAdapter(Bundle.Version.XmlAdapter::class)
    var version: Bundle.Version? = null
        private set

    /** Bundle platform identifier */
    @XmlAttribute
    @XmlJavaTypeAdapter(PlatformId.XmlAdapter::class)
    var platform: PlatformId? = null
        private set

    /** File entries */
    @XmlElement(name = "file")
    var fileEntries: List<Bundle.FileEntry> = ArrayList()
        private set

    /** Java version */
    @XmlAttribute
    var javaVersion: String = SystemUtils.JAVA_VERSION
        private set

    /**
     * @param path Bundle path
     * @param name Bundle name
     * @param version Bundle version
     * @param platform Platform identifier
     * @param fileEntries File entries contained in this bundle
     * @param javaVersion Java version
     */
    @JvmOverloads constructor(
            path: File? = null,
            name: String? = null,
            version: Bundle.Version? = null,
            platform: PlatformId? = null,
            fileEntries: List<Bundle.FileEntry> = ArrayList(),
            javaVersion: String = SystemUtils.JAVA_VERSION) {
        this.path = path
        this.name = name
        this.version = version
        this.platform = platform
        this.fileEntries = fileEntries
        this.javaVersion = javaVersion
    }

    /** Bundle content path */
    val contentPath: File by lazy(LazyThreadSafetyMode.NONE, {
        val nioBasePath: Path = this.path!!.toPath()
        val nioContentPath: Path
        if (this.platform!!.operatingSystem == OperatingSystem.OSX) {
            nioContentPath = nioBasePath
                    .resolve("Contents")
        } else {
            nioContentPath = nioBasePath
        }

        nioContentPath.toFile()
    })

    /** Jar path */
    val jarPath: File by lazy(LazyThreadSafetyMode.NONE, {
        if (this.platform!!.operatingSystem == OperatingSystem.OSX) {
            File(this.contentPath, "Java")
        } else {
            File(this.contentPath, "app")
        }
    })

    /** Manifest file path */
    val manifestFile: File by lazy(LazyThreadSafetyMode.NONE, {
        Bundle.manifestFile(this.path!!)
    })

    /** Bumdle configuration file */
    val configFile: File by lazy(LazyThreadSafetyMode.NONE, {
        var nioConfigFile = Files.find(this.jarPath.toPath(), 1, BiPredicate {
            p, a ->
            a.isRegularFile && p.fileName.toString().endsWith(".cfg")
        }
        ).findFirst()

        if (!nioConfigFile.isPresent)
            throw IllegalStateException("Config file not found within jar path [${this.jarPath}]")

        nioConfigFile.get().toFile()
    })

    val configuration: Configuration by lazy(LazyThreadSafetyMode.NONE) { -> Configuration() }

    /**
     * Manifest file entry
     */
    data class FileEntry(
            @XmlAttribute
            val uriPath: String? = null,
            @XmlAttribute
            val md5: String = "") {}

    /**
     * Verification exception
     */
    class VerificationException(message: String) : Exception(message) {}

    /**
     * Static methods
     */
    companion object {
        val MANIFEST_FILENAME = "manifest.xml"

        fun hashFile(file: File): String {
            return com.google.common.io.Files.hash(file, Hashing.md5()).toString()
        }

        /**
         * Search manifest file within bundle path
         * @param path Bundle path
         * @return Manifest file or null if not found
         */
        private fun manifestFile(path: File): File {
            return File(path, MANIFEST_FILENAME)
        }

        /**
         * Create bundle instance and stores manifest within bundle path
         * @param bundlePath Path of bundle. The name of the folder must be a parsable platform id (eg. osx64)
         * @param bundleNme Name of the bundle to create
         * @param version Version of the bundle
         */
        @JvmStatic fun create(bundlePath: File, bundleName: String, platformId: PlatformId, version: Version): Bundle {
            val fileEntries = ArrayList<FileEntry>()

            // Walk bundle directory and calculate md5 for each regular file
            var pathUri = bundlePath.toURI()
            var nPath = Paths.get(pathUri)

            // Remove existing manifest
            var manifestFile = Bundle.manifestFile(bundlePath)
            if (manifestFile.exists()) manifestFile.delete()

            Files.walk(nPath)
                    .filter { p ->
                        val filename = p.fileName.toString()
                        // Exclude file specific patterns from manifest
                        java.nio.file.Files.isRegularFile(p) &&
                                !filename.startsWith('.')
                    }
                    .forEach { p ->
                        fileEntries.add(FileEntry(
                                // Store relative path, simply cutting at root path length, including the slash
                                uriPath = p.toUri().toString().substring(pathUri.toString().length + 2),
                                md5 = this.hashFile(p.toFile())))
                    }

            // Create bundle instance
            var bundle = Bundle(bundlePath, bundleName, version, platformId, fileEntries)

            // Serialize bundle to manifest
            var context = JAXBContext.newInstance(Bundle::class.java)
            var m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            var os = FileOutputStream(bundle.manifestFile).buffered()
            try {
                m.marshal(bundle, os)
            } finally {
                os.close()
            }

            return bundle
        }

        /**
         * Load bundle from manifest/path
         * @param bundlePath Bundle path
         */
        @JvmStatic fun load(bundlePath: File): Bundle {
            val manifestFile = Bundle.manifestFile(bundlePath)

            var context = JAXBContext.newInstance(Bundle::class.java)
            var m = context.createUnmarshaller();
            var inputStream = FileInputStream(manifestFile).buffered()
            try {
                var bundle = m.unmarshal(inputStream) as Bundle
                bundle.path = bundlePath
                return bundle
            } finally {
                inputStream.close()
            }
        }
    }

    /**
     * Verify manifest against files in a path
     */
    fun verify() {
        val nPath = Paths.get(this.path!!.toURI())

        // Hashed check list to verify left-overs
        var checkList = this.fileEntries.toMapBy({ s -> s.uriPath }) as HashMap

        for (entry in this.fileEntries.asSequence().filter { e -> !e.uriPath!!.equals(MANIFEST_FILENAME) }) {
            val path = Paths.get(nPath.toUri().resolve(entry.uriPath))
            if (!Files.exists(path))
                throw VerificationException("File [${path} does not exist")

            val md5 = Bundle.hashFile(path.toFile())
            if (!entry.md5.equals(md5))
                throw VerificationException("File [${path}] has invalid md5 [${md5}] expected [${entry.md5}]")

            checkList.remove(entry.uriPath)
        }

        if (checkList.size > 0) {
            this.log.warn("Excess files detected during manifest verification [${checkList.keys.joinToString(",")}]")
        }
    }

    override fun toString(): String {
        return "${javaClass.simpleName}(name=${name}, version=${version}, platform=${this.platform}, javaVersion=${javaVersion})"
    }

    /**
     * Bundle version
     * @param components Numeric version components
     * @param suffix An optional version suffix
     * Created by masc on 24.08.15.
     */
    data class Version(
            val components: List<Int>,
            val suffix: String
    ) : Comparable<Version>, Serializable {
        /**
         * Adapter for xml serialization
         * */
        class XmlAdapter : javax.xml.bind.annotation.adapters.XmlAdapter<String, Version>() {
            override fun marshal(v: Version?): String? {
                return v.toString()
            }

            override fun unmarshal(v: String?): Version? {
                return if (v == null) null else Version.parse(v)
            }
        }

        companion object {
            /**
             * Parse version string
             * @param version Version string
             */
            @JvmStatic fun parse(version: String): Version {
                // Determine end of numeric components
                var end = version.indexOfFirst({ c -> !c.isDigit() && c != '.' })

                var suffix: String
                if (end < 0) {
                    end = version.length
                    suffix = ""
                } else {
                    // Cut and trim suffix
                    suffix = version.substring(end).trim { c -> c.isWhitespace() || "-._".contains(c) }
                }

                // Parse components to ints
                val components: List<Int> = if (end > 0)
                    version.substring(0, end).split('.').map({ s -> s.toInt() })
                else
                    ArrayList<Int>()

                if (components.size == 0)
                    throw IllegalArgumentException("Empty version string [${version}]")

                return Version(components, suffix)
            }

            fun tryParse(version: String): Version? {
                return try {
                    this.parse(version)
                } catch(e: Exception) {
                    null
                }
            }
        }

        /**
         * Comparable implementation
         */
        override fun compareTo(other: Version): Int {
            val tSize = this.components.size
            val oSize = other.components.size

            val less = if (tSize < oSize) this else other

            // Compare version components
            for (i in 0..less.components.size - 1) {
                val c = this.components[i].compareTo(other.components[i])
                if (c != 0)
                    return c
            }

            // Revert to suffix comparison if version components were equal and both have the same amount of components
            if (tSize == oSize) {
                return this.suffix.compareTo(other.suffix)
            }

            // Otherwise the version with more components wins
            return if (tSize > oSize) 1 else -1
        }

        /**
         * Equals implementation
         */
        override fun equals(other: Any?): Boolean {
            return if (other is Version) this.compareTo(other) == 0 else false
        }

        override fun toString(): String {
            return this.components.joinToString(".") +
                    if (this.suffix.length > 0) "-" + suffix else ""
        }
    }


    /**
     * Native packager bundle configuration (file)
     * Created by masc on 10.09.15.
     */
    inner class Configuration() {
        private val KEY_APP_MAINJAR = "app.mainjar"
        private val KEY_APP_VERSION = "app.version"
        private val KEY_APP_CLASSPATH = "app.classpath"

        /** Contains key/value pairs or plain strings (non parsable lines) */
        private val entries = ArrayList<Any>()
        private val entryMap = LinkedHashMap<String, String>()

        /** Application main jar */
        var appMainJar: String
            get() {
                return entryMap.get(KEY_APP_MAINJAR) ?: ""
            }
            set(value: String) {
                entryMap.set(KEY_APP_MAINJAR, value)
            }

        /** Application version */
        var appVersion: String
            get() {
                return entryMap.get(KEY_APP_VERSION) ?: ""
            }
            set(value: String) {
                entryMap.set(KEY_APP_VERSION, value)
            }

        /** Application class path */
        var appClassPath: List<String>
            get() {
                return entryMap.get(KEY_APP_CLASSPATH)?.split(':', ';') ?: ArrayList()
            }
            set(value: List<String>) {
                entryMap.set(KEY_APP_CLASSPATH, value.joinToString(";"))
            }

        /**
         * c'tor.
         * Loads configuration from file
         **/
        init {
            val reader = FileReader(this@Bundle.configFile).buffered()

            try {
                // All property entries starting with 'app.'
                val re = Regex("^(app\\..+)=(.*)$")
                var line: String? = null

                val read = { line = reader.readLine(); line != null }
                while (read()) {
                    val entry: Any
                    val mr = re.find(line!!)
                    if (mr != null) {
                        entry = Pair(mr.groups.get(1)!!.value, mr.groups.get(2)!!.value)
                        entryMap.putAll(entry)
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
        fun save() {
            val writer = FileWriter(this@Bundle.configFile).buffered()

            try {
                for (entry in entries) {
                    val outputLine = if (entry is Pair<*, *>) {
                        // Get current entry value from entry map
                        val v = this.entryMap.getRaw(entry.first)
                        "${entry.first}=${v}"
                    } else entry.toString()
                    writer.write(outputLine)
                    writer.write(if (this@Bundle.platform!!.operatingSystem == OperatingSystem.WINDOWS) "\r\n" else "\n")
                }
            } finally {
                writer.close()
            }
        }
    }

    /**
     * Execute bundle process
     * @param args Arguments
     */
    fun execute(elevate: Boolean = false, vararg args: String) {
        val error = StringBuffer()

        val command = ArrayList<String>()
        if (SystemUtils.IS_OS_MAC_OSX) {
            command.add("open")
            command.add(this.path!!.toString())
            command.add("--args")
        } else {
            if (elevate) {
                command.add(ProcessElevation.executable.file.toString())
                command.add("elevate")
            }
            command.add(File(this.path!!, this.name).toString())
        }
        command.addAll(args)

        val pb = ProcessBuilder(command)
        val pe = ProcessExecutor(pb, errorHandler = ProcessExecutor.DefaultStreamHandler(
                trim = true,
                omitEmptyLines = true,
                collectInto = error))
        pe.start()
        try {
            pe.waitFor()
        } catch(e: Exception) {
            if (error.length > 0) log.error(error.toString())
            throw e
        }
    }
}

// Extension methods

/**
 * Extension method for filtering an iterable of versions
 * @param pattern Pattern to match
 */
fun Iterable<Bundle.Version>.filter(pattern: String): Iterable<Bundle.Version> {
    val l = pattern
            // Split by wildcard (+) including empty elements
            .split(Regex("\\+"), 0)
            // Replace with regex wildcard and quote content
            .map { p -> if (p.length > 0) Pattern.quote(p) else ".+" }

    val re = Regex("^" + l.joinToString("") + "$")

    return this.filter { v -> re.matches(v.toString()) }
}
