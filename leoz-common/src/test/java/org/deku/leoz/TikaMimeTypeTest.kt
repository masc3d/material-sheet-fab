package org.deku.leoz

import org.apache.tika.mime.MimeTypes
import org.junit.Assert
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.log.slf4j.*

/**
 * Created by masc on 16.12.17.
 */
class TikaMimeTypeTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testCustomMimeTypes() {
        MimeTypes.getDefaultMimeTypes().forName(MimeType.LEOZ_DIAGNOSTIC_ZIP.value).also {
            Assert.assertTrue(it.name.length > 0)
            Assert.assertTrue(it.extension.length > 0)
        }
    }
}