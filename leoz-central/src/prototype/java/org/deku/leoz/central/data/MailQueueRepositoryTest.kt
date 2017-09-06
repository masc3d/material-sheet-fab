package org.deku.leoz.central.data

import org.deku.leoz.central.config.DataTestConfiguration
import org.deku.leoz.central.data.repository.MailQueueRepository
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.junit.PrototypeTest
import javax.inject.Inject

@Category(PrototypeTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class
))
class MailQueueRepositoryTest {

    @Inject
    private lateinit var mailRepository: MailQueueRepository

    @Test
    fun testInsertWithValidReceiver() {
        mailRepository.insertSms(receiver = "491721781082", message = "Test message")
    }
}