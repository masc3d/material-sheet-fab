package org.deku.leoz.build

import com.google.common.hash.Hashing
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.Serializable
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
import kotlin.text.Regex

/**
 * Leoz artifact test
 * Created by masc on 22.08.15.
 */
public data class Artifact(val type:Artifact.Type, val version: Artifact.Version) {

    /**
     * Leoz artifact type
     */
    public enum class Type(val artifactType: String) {
        LEOZ_CENTRAL("leoz-central"),
        LEOZ_NODE("leoz-node"),
        LEOZ_UI("leoz-ui"),
        LEOZ_BOOT("leoz-boot");

        override fun toString(): String {
            return this.artifactType
        }
    }

    /**
     * Artifact version
     * Created by masc on 24.08.15.
     */
    public data class Version(val components: List<Int>, val suffix: String) : Comparable<Version>, Serializable {
        /** Adapter for xml serialization */
        class XmlAdapter : javax.xml.bind.annotation.adapters.XmlAdapter<String, Version>() {
            override fun marshal(v: Version?): String? { return v.toString() }
            override fun unmarshal(v: String?): Version? { return if (v == null) null else Version.parse(v) }
        }

        public companion object {
            @platformStatic public fun parse(version: String): Version {
                // Determine end of numeric components
                var end = version.indexOfFirst( { c -> !c.isDigit() && c != '.' } )

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
                    version.substring(0, end).split('.').map( { s -> s.toInt() } )
                else
                    ArrayList<Int>()

                if (components.size() == 0)
                    throw IllegalArgumentException("Empty version string [${version}]")

                return Version(components, suffix)
            }

            public fun tryParse(version: String): Version? {
                return try { this.parse(version) } catch(e: Exception) { null }
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
     * Leoz artifact manifest
     * Created by masc on 25.08.15.
     */
    @XmlRootElement
    public class Manifest(
            @XmlAttribute
            @XmlJavaTypeAdapter(javaClass<Version.XmlAdapter>())
            public val version: Version? = null,
            @XmlElement(name="file")
            /** File entries */
            public val fileEntries: List<Manifest.FileEntry> = ArrayList(),
            /** Java version */
            @XmlAttribute
            public val javaVersion: String = SystemUtils.JAVA_VERSION) : Serializable {

        val log = LogFactory.getLog(this.javaClass)
        /**
         * Manifest file entry
         */
        @XmlElement
        public data class FileEntry(
                @XmlAttribute
                val uri: URI? = null,
                @XmlAttribute
                val md5: String = "") { }

        /**
         * Verification exception
         */
        public class VerificationException(message: String) : Exception(message) { }

        companion object {
            fun hashFile(file: File): String {
                return com.google.common.io.Files.hash(file, Hashing.md5()).toString()
            }

            /**
             * Create artifact manifest from path
             */
            @platformStatic public fun create(artifactPath: File, version: Version): Manifest {
                val fileEntries = ArrayList<FileEntry>()

                // Walk artifact directory and calculate md5 for each regular file
                var nioArtifactPath = Paths.get(artifactPath.toURI())
                Files.walk(nioArtifactPath).forEach { p ->
                    if (java.nio.file.Files.isRegularFile(p)) {
                        fileEntries.add(FileEntry(
                                uri = Paths.get(URI("file:/")).resolve(nioArtifactPath.relativize(p)).toUri(),
                                md5 = this.hashFile(p.toFile())))
                    }
                }

                return Manifest(version, fileEntries)
            }

            /**
             * Load manifest from file
             */
            @platformStatic public fun load(inputStream: InputStream): Manifest {
                var context = JAXBContext.newInstance(javaClass<Manifest>())
                var m = context.createUnmarshaller();
                return m.unmarshal(inputStream) as Manifest
            }
        }


        /**
         * Save manifest
         * @param os Output stream to save to
         */
        public fun save(os: OutputStream) {
            var context = JAXBContext.newInstance(javaClass<Manifest>())
            var m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            m.marshal(this, os);
        }

        /**
         * Verify manifest against files in a path
         */
        public fun verifyFiles(artifactPath: File) {
            val nPath = Paths.get(artifactPath.toURI())

            // Hashed check list to verify left-overs
            var checkList = this.fileEntries.toMap( { s -> s.uri } ) as HashMap

            for (entry in this.fileEntries) {
                val path = nPath.resolve(Paths.get(URI("file:/")).relativize(Paths.get(entry.uri)))
                if (!Files.exists(path))
                    throw VerificationException("File [${path} does not exist")

                val md5 = Manifest.hashFile(path.toFile())
                if (!entry.md5.equals(md5))
                    throw VerificationException("File [${path}] has invalid md5 [${md5}] expected [${entry.md5}]")

                checkList.remove(entry.uri)
            }

            if (checkList.size() > 0) {
                this.log.warn("Excess files detected during manifest creation [${checkList.keySet().joinToString(",")}")
            }
        }
    }
}