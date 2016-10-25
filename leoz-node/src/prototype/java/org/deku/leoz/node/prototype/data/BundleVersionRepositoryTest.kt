package org.deku.leoz.node.prototype.data

import org.deku.leoz.node.test.DataTest
import org.deku.leoz.node.data.jpa.MstBundleVersion
import org.deku.leoz.node.data.jpa.QMstCountry
import org.deku.leoz.node.data.repositories.master.BundleVersionRepository
import org.deku.leoz.node.data.repositories.master.CountryRepository
import org.junit.Test
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceContext
import sx.logging.slf4j.*

/**
 * Created by masc on 05.10.15.
 */
open class BundleVersionRepositoryTest : DataTest() {
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