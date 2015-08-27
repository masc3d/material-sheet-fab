package org.deku.leoz.build

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.google.common.hash.Hashing
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.logging.LogFactory
import java.io.*
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.*
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.*
import kotlin.platform.platformStatic


/**
 * Leoz artifact manifest
 * Created by masc on 25.08.15.
 */
@XmlRootElement
public class Manifest(
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
        @platformStatic public fun create(artifactPath: File): Manifest {
            val fileEntries = ArrayList<FileEntry>()

            // Walk artifact directory and calculate md5 for each regular file
            var nioArtifactPath = Paths.get(artifactPath.toURI())
            Files.walk(nioArtifactPath).forEach { p ->
                if (java.nio.file.Files.isRegularFile(p)) {
                    fileEntries.add(FileEntry(
                            uri = nioArtifactPath.relativize(p).toUri(),
                            md5 = this.hashFile(p.toFile())))
                }
            }

            return Manifest(fileEntries)
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
    public fun verifyFiles(path: File) {
        val nPath = Paths.get(path.toURI())

        // Hashed check list to verify left-overs
        var checkList = this.fileEntries.toMap( { s -> s.uri } ) as HashMap

        for (entry in this.fileEntries) {
            val path = nPath.resolve(Paths.get(entry.uri))
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
