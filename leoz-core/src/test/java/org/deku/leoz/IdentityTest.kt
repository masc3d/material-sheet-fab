package org.deku.leoz

import org.deku.leoz.bundle.BundleType
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.junit.StandardTest

/**
 * Created by masc on 27.06.15.
 */
@Category(StandardTest::class)
class IdentityTest {
    private var log = LoggerFactory.getLogger(this.javaClass)

    val identity by lazy {
        Identity.create(BundleType.LEOZ_NODE.value, SystemInformation.create())
    }

    @Test
    fun testIdentity() {
        log.info(this.identity.toString())
    }
}
