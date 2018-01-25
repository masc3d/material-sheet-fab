package org.deku.leoz.node.data

import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.node.data.jpa.MstBundleVersion
import org.deku.leoz.node.data.repository.BundleVersionRepository
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import sx.junit.PrototypeTest
import javax.inject.Inject

/**
 * Created by masc on 05.10.15.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(DataTestConfiguration::class))
@Category(PrototypeTest::class)
open class BundleVersionRepositoryTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    lateinit private var bundleVersionRepository: BundleVersionRepository

    @Test
    @Transactional
    open fun testSaveWithAutoIdentity() {
        val record = MstBundleVersion()
        record.alias = "test"
        record.bundle = "test"
        record.version = "test"

        bundleVersionRepository.save(record)

        bundleVersionRepository.findAll().forEach {
            log.info("${it.id} ${it.version}")
        }
    }
}