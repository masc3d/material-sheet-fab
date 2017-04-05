package sx.util.zip

import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.IOException
import java.util.zip.ZipException
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

/**
 * Verify zip archive
 * Created by masc on 19/02/2017.
 */
fun ZipFile.verify() {
    val log = LoggerFactory.getLogger(this.javaClass)

    val entries = this.entries()
    while (entries.hasMoreElements()) {
        val entry = entries.nextElement()
        // Retrieving stream input is part of the verification
        this.getInputStream(entry)
        val crc = entry.crc
        val compressedSize = entry.compressedSize
        val name = entry.name

        log.debug("Verifying [${name}] crc [${crc}] compressedSize [${compressedSize}]")
    }
}