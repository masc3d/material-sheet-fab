package org.deku.leoz.build

import com.google.common.hash.Hashing
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import sx.platform.PlatformId
import java.io.*
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.util
import java.util.*
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import kotlin.platform.platformStatic
import kotlin.properties.Delegates
import kotlin.text.Regex

/**
 * Represents a local/physical leoz artifact and its manifest at the same time
 * Created by masc on 22.08.15.
 */
@XmlRootElement
public data class Artifact(
        /** Path of artifact */
        path: File? = null,
        /** Name */
        @XmlAttribute
        public val name: String? = null,
        /** Version */
        @XmlAttribute
        @XmlJavaTypeAdapter(Artifact.Version.XmlAdapter::class)
        public val version: Artifact.Version? = null,
        @XmlAttribute
        @XmlJavaTypeAdapter(PlatformId.XmlAdapter::class)
        public val platform: PlatformId? = null,
        @XmlElement(name = "file")
        /** File entries */
        public val fileEntries: List<Artifact.FileEntry> = ArrayList(),
        /** Java version */
        @XmlAttribute
        public val javaVersion: String = SystemUtils.JAVA_VERSION) : Serializable {

    private val log = LogFactory.getLog(this.javaClass)

    public var path: File? = null
        private set

    init {
        this.path = path
    }

    /**
     * Manifest file entry
     */
    @XmlElement
    public data class FileEntry(
            @XmlAttribute
            val uriPath: String? = null,
            @XmlAttribute
            val md5: String = "") {}

    /**
     * Verification exception
     */
    public class VerificationException(message: String) : Exception(message) {}

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
        @platformStatic public fun create(path: File, name: String, version: Version): Artifact {
            val fileEntries = ArrayList<FileEntry>()

            var platformId = PlatformId.parse(path.getName())

            // Walk artifact directory and calculate md5 for each regular file
            var pathUri = path.toURI()
            var nPath = Paths.get(pathUri)
            Files.walk(nPath)
                    .filter { p ->
                        val filename = p.getFileName().toString()
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
            var artifact = Artifact(path, name, version, platformId, fileEntries)

            // Serialize artifact to manifest
            var context = JAXBContext.newInstance(javaClass<Artifact>())
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
         * @param artifactPath Artifact path
         */
        @platformStatic public fun load(artifactPath: File): Artifact {
            var context = JAXBContext.newInstance(javaClass<Artifact>())
            var m = context.createUnmarshaller();
            var inputStream = FileInputStream(File(artifactPath, MANIFEST_FILENAME)).buffered()
            try {
                var artifact = m.unmarshal(inputStream) as Artifact
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
    public fun verify() {
        val nPath = Paths.get(this.path!!.toURI())

        // Hashed check list to verify left-overs
        var checkList = this.fileEntries.toMap({ s -> s.uriPath }) as HashMap

        for (entry in this.fileEntries.asSequence().filter { e -> !e.uriPath!!.equals(MANIFEST_FILENAME) }) {
            val path = Paths.get(nPath.toUri().resolve(entry.uriPath))
            if (!Files.exists(path))
                throw VerificationException("File [${path} does not exist")

            val md5 = Artifact.hashFile(path.toFile())
            if (!entry.md5.equals(md5))
                throw VerificationException("File [${path}] has invalid md5 [${md5}] expected [${entry.md5}]")

            checkList.remove(entry.uriPath)
        }

        if (checkList.size() > 0) {
            this.log.warn("Excess files detected during manifest verification [${checkList.keySet().joinToString(",")}]")
        }
    }

    override fun toString(): String {
        return "${javaClass.getSimpleName()}(name=${name}, version=${version}, platform=${this.platform}, javaVersion=${javaVersion})"
    }

    /**
     * Artifact version
     * Created by masc on 24.08.15.
     */
    public data class Version(val components: List<Int>, val suffix: String) : Comparable<Version>, Serializable {
        /** Adapter for xml serialization */
        class XmlAdapter : javax.xml.bind.annotation.adapters.XmlAdapter<String, Version>() {
            override fun marshal(v: Version?): String? {
                return v.toString()
            }

            override fun unmarshal(v: String?): Version? {
                return if (v == null) null else Version.parse(v)
            }
        }

        public companion object {
            @platformStatic public fun parse(version: String): Version {
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

            public fun tryParse(version: String): Version? {
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
}