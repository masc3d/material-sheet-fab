package org.deku.leoz.bundle

import com.google.common.hash.Hashing
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import sx.platform.OperatingSystem
import sx.platform.PlatformId
import java.io.*
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util
import java.util.*
import java.util.function.BiPredicate
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import kotlin.properties.Delegates
import kotlin.text.Regex

/**
 * Represents a local/physical leoz bundle including a manifest containing metadata
 * Created by masc on 22.08.15.
 */
@XmlRootElement
class Bundle : Serializable {

    private val log = LogFactory.getLog(this.javaClass)

    var path: File? = null
        private set

    @XmlAttribute
    var name: String? = null
        private set

    @XmlAttribute
    @XmlJavaTypeAdapter(Bundle.Version.XmlAdapter::class)
    var version: Bundle.Version? = null
        private set

    @XmlAttribute
    @XmlJavaTypeAdapter(PlatformId.XmlAdapter::class)
    var platform: PlatformId? = null
        private set

    @XmlElement(name = "file")
    /** File entries */
    var fileEntries: List<Bundle.FileEntry> = ArrayList()
        private set

    @XmlAttribute
    var javaVersion: String = SystemUtils.JAVA_VERSION
        private set

    @JvmOverloads constructor(/** Path of artifact */
                              path: File? = null,
                              /** Name */
                              name: String? = null,
                              /** Version */
                              version: Bundle.Version? = null,
                              platform: PlatformId? = null,
                              /** File entries */
                              fileEntries: List<Bundle.FileEntry> = ArrayList(),
                              /** Java version */
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
                    .resolve(this.name + ".app")
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

    /** Bumdle configuration file */
    val configFile: File by lazy(LazyThreadSafetyMode.NONE, {
        var nioConfigFile = Files.find(this.jarPath.toPath(), 1, BiPredicate { p, a -> a.isRegularFile && p.fileName.toString().endsWith(".cfg") }).findFirst()
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
         * Create artifact instance and stores manifest within artifact path
         * @param path Path of artifact. The name of the folder must be a parsable platform id (eg. osx64)
         * @param name Name of the artifact to create
         * @param version Version of the artifact
         */
        @JvmStatic fun create(path: File, name: String, version: Version): Bundle {
            val fileEntries = ArrayList<FileEntry>()

            var platformId = PlatformId.parse(path.name)

            // Walk artifact directory and calculate md5 for each regular file
            var pathUri = path.toURI()
            var nPath = Paths.get(pathUri)
            Files.walk(nPath)
                    .filter { p ->
                        val filename = p.fileName.toString()
                        // Exclude file specific patterns from manifest
                        java.nio.file.Files.isRegularFile(p) &&
                                !filename.equals(MANIFEST_FILENAME) &&
                                !filename.startsWith('.')
                    }
                    .forEach { p ->
                        fileEntries.add(FileEntry(
                                // Store relative path, simply cutting at root path length, including the slash
                                uriPath = p.toUri().toString().substring(pathUri.toString().length() + 2),
                                md5 = this.hashFile(p.toFile())))
                    }

            // Create artifact instance
            var artifact = Bundle(path, name, version, platformId, fileEntries)

            // Serialize artifact to manifest
            var context = JAXBContext.newInstance(Bundle::class.java)
            var m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            var os = FileOutputStream(File(path, MANIFEST_FILENAME)).buffered()
            try {
                m.marshal(artifact, os)
            } finally {
                os.close()
            }

            return artifact
        }

        /**
         * Load artifact from manifest/path
         * @param artifactPath Bundle path
         */
        @JvmStatic fun load(artifactPath: File): Bundle {
            var context = JAXBContext.newInstance(Bundle::class.java)
            var m = context.createUnmarshaller();
            var inputStream = FileInputStream(File(artifactPath, MANIFEST_FILENAME)).buffered()
            try {
                var artifact = m.unmarshal(inputStream) as Bundle
                artifact.path = artifactPath
                return artifact
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
        var checkList = this.fileEntries.toMap({ s -> s.uriPath }) as HashMap

        for (entry in this.fileEntries.asSequence().filter { e -> !e.uriPath!!.equals(MANIFEST_FILENAME) }) {
            val path = Paths.get(nPath.toUri().resolve(entry.uriPath))
            if (!Files.exists(path))
                throw VerificationException("File [${path} does not exist")

            val md5 = Bundle.hashFile(path.toFile())
            if (!entry.md5.equals(md5))
                throw VerificationException("File [${path}] has invalid md5 [${md5}] expected [${entry.md5}]")

            checkList.remove(entry.uriPath)
        }

        if (checkList.size() > 0) {
            this.log.warn("Excess files detected during manifest verification [${checkList.keySet().joinToString(",")}]")
        }
    }

    override fun toString(): String {
        return "${javaClass.simpleName}(name=${name}, version=${version}, platform=${this.platform}, javaVersion=${javaVersion})"
    }

    /**
     * Bundle version
     * Created by masc on 24.08.15.
     */
    data class Version(val components: List<Int>, val suffix: String) : Comparable<Version>, Serializable {
        /** Adapter for xml serialization */
        class XmlAdapter : javax.xml.bind.annotation.adapters.XmlAdapter<String, Version>() {
            override fun marshal(v: Version?): String? {
                return v.toString()
            }

            override fun unmarshal(v: String?): Version? {
                return if (v == null) null else Version.parse(v)
            }
        }

        companion object {
            @JvmStatic fun parse(version: String): Version {
                // Determine end of numeric components
                var end = version.indexOfFirst({ c -> !c.isDigit() && c != '.' })

                var suffix: String
                if (end < 0) {
                    end = version.length()
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

                if (components.size() == 0)
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
            val tSize = this.components.size()
            val oSize = other.components.size()

            val less = if (tSize < oSize) this else other

            // Compare version components
            for (i in 0..less.components.size() - 1) {
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

        override fun toString(): String {
            return this.components.joinToString(".") +
                    if (this.suffix.length() > 0) "-" + suffix else ""
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

        var appVersion: String
            get() {
                return entryMap.get(KEY_APP_VERSION) ?: ""
            }
            set(value: String) {
                entryMap.set(KEY_APP_VERSION, value)
            }

        var appClassPath: List<String>
            get() {
                return entryMap.get(KEY_APP_CLASSPATH)?.split(':', ';') ?: ArrayList()
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
        fun save() {
            val writer = FileWriter(this@Bundle.configFile).buffered()

            try {
                for (entry in entries) {
                    val outputLine = if (entry is Pair<*, *>) {
                        // Get current entry value from entry map
                        val v = this.entryMap.get(entry.first)
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
}